package com.example.personalfinance.datalayer.local.repositories;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.personalfinance.datalayer.local.daos.CategoryDao;
import com.example.personalfinance.datalayer.local.daos.ItemDao;
import com.example.personalfinance.datalayer.local.daos.UserDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.daos.auxiliry.DeletedRowDao;
import com.example.personalfinance.datalayer.local.dataclass.TransactWithCategory;
import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.entities.Item;
import com.example.personalfinance.datalayer.local.entities.auxiliry.DeletedRow;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.workers.SyncStateManager;
import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.TransactDao;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.fragment.transaction.transaction.model.Filter;
import com.example.personalfinance.fragment.transaction.transaction.model.ItemModel;
import com.example.personalfinance.fragment.transaction.transaction.model.TransactModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TransactRepository {
    private static final String TAG = "kiev";
    private final TransactDao transactDao;
    private final CategoryDao categoryDao;
    private final WalletDao walletDao;
    private final UserDao userDao;
    private final DeletedRowDao deletedRowDao;
    private final ItemDao itemDao;

    private enum Action {insert, delete}

    public TransactRepository(Context context) {
        AppLocalDatabase instance = AppLocalDatabase.getInstance(context);
        transactDao = instance.getTransactDao();
        deletedRowDao = instance.getDeletedRowDao();
        categoryDao = instance.getCategoryDao();
        walletDao = instance.getWalletDao();
        itemDao = instance.getItemDao();
        userDao = instance.getUserDao();
    }

    public Completable insertTransact(TransactModel transactModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    //get user currency
                    Currency currency = userDao.getCurrency();

                    //insert transact
                    transactModel.setTran_amount(UserRepository.backCurrency(transactModel.getTran_amount(), currency));
                    long id = transactDao.insertTransact(toTransact(transactModel, SyncStateManager.Action.insert, null));

                    //insert item if transact is a bill
                    if (transactModel.getType() == Transact.Type.bill)
                        for(ItemModel item : transactModel.getItems())
                            itemDao.insertItem(toItem(item, (int)id, SyncStateManager.determineSyncState(SyncStateManager.Action.insert, null)));

                    updateWalletAmount(transactModel, Action.insert);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteTransact(TransactModel transactModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    //if this is a bill then delete item first
                    if (transactModel.getType() == Transact.Type.bill)
                        for (ItemDao.IdAndSyncstate idAndSyncstate : itemDao.getIdAndSyncstate(transactModel.getTran_id())){
                            if (idAndSyncstate.syncState == SyncState.synced)
                                deletedRowDao.insert(new DeletedRow(idAndSyncstate.id, DeletedRow.Table.item));
                            itemDao.deleteItem(idAndSyncstate.id);
                        }

                    //delete transact
                    SyncState currentState = transactDao.getState(transactModel.getTran_id());
                    if (currentState == SyncState.synced)
                        deletedRowDao.insert(new DeletedRow(transactModel.getTran_id(), DeletedRow.Table.transact));
                    transactDao.deleteTransact(transactModel.getTran_id());
                    //after delete transact, wallet is then updated
                    updateWalletAmount(transactModel, Action.delete);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateTransact(TransactModel transactModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    //get user currency
                    Currency currency = userDao.getCurrency();

                    //update transact
                    SyncState currentStateTransact = transactDao.getState(transactModel.getTran_id());
                    transactModel.setTran_amount(UserRepository.backCurrency(transactModel.getTran_amount(), currency));
                    transactDao.updateTransact(toTransact(transactModel, SyncStateManager.Action.update, currentStateTransact));

                    //update for each item if it is a bill
                    if (transactModel.getType() == Transact.Type.bill)
                        for (ItemModel item : transactModel.getItems()){
                            SyncState currentStateItem = itemDao.getState(item.getId());
                            itemDao.updateItem(toItem(item, item.getBill_id(), SyncStateManager.determineSyncState(SyncStateManager.Action.update, currentStateItem)));
                        }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void updateWalletAmount(TransactModel transactModel, Action action) throws RuntimeException {
        //update the state of wallet before update wallet amount
        SyncState currentState = walletDao.getState(transactModel.getWallet_id());
        walletDao.setState(transactModel.getWallet_id(), SyncStateManager.determineSyncState(SyncStateManager.Action.update, currentState));

        //update wallet amount
        switch (transactModel.getCategoryModel().getCategoryType()){
            case spending:
                switch (action){
                    case insert:
                        walletDao.spendAmount(transactModel.getWallet_id(), transactModel.getTran_amount());
                        break;
                    case delete:
                        walletDao.earnAmount(transactModel.getWallet_id(), transactModel.getTran_amount());
                }
                break;
            case earning:
                switch (action){
                    case insert:
                        walletDao.earnAmount(transactModel.getWallet_id(), transactModel.getTran_amount());
                        break;
                    case delete:
                        walletDao.spendAmount(transactModel.getWallet_id(), transactModel.getTran_amount());
                }
                break;
            default:
                throw new RuntimeException("something happen in update wallet amount");
        }
    }

    public Single<TransactModel> getTransact(Integer id){
        return transactDao
                .getTransact(id)
                .map(transactWithCategory -> toTransactModel(transactWithCategory.transact, transactWithCategory.category))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<List<TransactModel>> getAllByWalletId(Integer walletId, Filter filter){
        return transactDao
                .getAllTransactBelongTo(walletId, filter.from, filter.to)
                .map(transactWithCategories -> {
                    //filter
                    return transactWithCategories
                            .stream()
                            /*to TransactModel*/.map(tran -> toTransactModel(tran.transact, tran.category))
                            /*category*/.filter(tran -> {
                                if (filter.categories == null || filter.categories.isEmpty()) return true;
                                else return filter.categories.contains(tran.getCategoryModel());
                            })
                            /*type*/.filter(tran -> {
                                if (filter.type == Filter.Type.all) return true;
                                else{
                                    if (filter.type == Filter.Type.transaction && tran.getType() == Transact.Type.transaction) return true;
                                    else if (filter.type == Filter.Type.bill && tran.getType() == Transact.Type.bill) return true;
                                    else return false;
                                }
                            })
                            /*title*/.filter(tran -> {
                                if (filter.title == null || filter.title.isEmpty()) return true;
                                else if (tran.getTran_title().toLowerCase().contains(filter.title.toLowerCase())) return true;
                                else return false;
                            })
                            /*sort*/.sorted((tran1, tran2) -> {
                                switch(filter.sort){
                                    case high_to_low:
                                        return  - Double.compare(tran1.getTran_amount(), tran2.getTran_amount());
                                    case low_to_high:
                                        return Double.compare(tran1.getTran_amount(), tran2.getTran_amount());
                                    case oldest:
                                        return tran1.getDate_time().compareTo(tran2.getDate_time());
                                    case latest: default:
                                        return - tran1.getDate_time().compareTo(tran2.getDate_time());
                                }
                            })
                            .collect(Collectors.toList());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Double> getWalletAmount(Integer walletId){
        Single<Double> amount = walletDao.getWalletAmount(walletId);
        Single<Currency> currency = userDao.getSingleCurrency();
        return Single.zip(amount, currency, UserRepository::toCurrency)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<ItemModel>> getAllItemBelongTo(Integer billId){
        return itemDao
                .getAllItemBelongTo(billId)
                .map(items -> {
                    List<ItemModel> itemModels = new ArrayList<>();
                    items.forEach(item -> itemModels.add(toItemModel(item)));
                    return itemModels;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Transact toTransact(TransactModel transactModel, SyncStateManager.Action action, SyncState currentState){
        Transact transact = new Transact();
        transact.setId(transactModel.getTran_id());
        transact.setWallet_id(transactModel.getWallet_id());
        transact.setTitle(transactModel.getTran_title());
        transact.setCategory_id(transactModel.getCategoryModel().getId());
        transact.setType(transactModel.getType());
        transact.setDate_time(transactModel.getDate_time());
        transact.setAmount(transactModel.getTran_amount());
        transact.setAuto_tran(transactModel.getAuto_tran());
        transact.setDescription(transactModel.getTran_description());
        transact.setSyncState(SyncStateManager.determineSyncState(action, currentState));
        return transact;
    }

    public static TransactModel toTransactModel(Transact transact, Category category){
        TransactModel transactModel = new TransactModel();
        transactModel.setTran_id(transact.getId());
        transactModel.setWallet_id(transact.getWallet_id());
        transactModel.setTran_title(transact.getTitle());
        transactModel.setType(transact.getType());
        transactModel.setCategoryModel(CategoryRepository.toCategoryModel(category));
        transactModel.setDate_time(transact.getDate_time());
        transactModel.setTran_amount(transact.getAmount());
        transactModel.setAuto_tran(transact.getAuto_tran());
        transactModel.setTran_description(transact.getDescription());
        return transactModel;
    }

    public static Item toItem(ItemModel itemModel, Integer billId, SyncState state){
        Item item = new Item();
        item.setId(itemModel.getId());
        item.setBill_id(billId);
        item.setItem_name(itemModel.getItem_name());
        item.setQuantity(itemModel.getQuantity());
        item.setItem_price(itemModel.getItem_price());
        item.setSyncState(state);
        return item;
    }

    public static ItemModel toItemModel(Item item){
        ItemModel itemModel = new ItemModel();
        itemModel.setId(item.getId());
        itemModel.setBill_id(item.getBill_id());
        itemModel.setItem_name(item.getItem_name());
        itemModel.setQuantity(item.getQuantity());
        itemModel.setItem_price(item.getItem_price());
        return itemModel;
    }
}
