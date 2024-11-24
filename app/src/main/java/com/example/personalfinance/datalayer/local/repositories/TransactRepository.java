package com.example.personalfinance.datalayer.local.repositories;

import android.content.Context;

import com.example.personalfinance.datalayer.local.daos.CategoryDao;
import com.example.personalfinance.datalayer.local.daos.ItemDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.daos.auxiliry.DeletedRowDao;
import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.entities.Item;
import com.example.personalfinance.datalayer.local.entities.auxiliry.DeletedRow;
import com.example.personalfinance.datalayer.workers.SyncStateManager;
import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.TransactDao;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.fragment.transaction.transaction.model.ItemModel;
import com.example.personalfinance.fragment.transaction.transaction.model.TransactModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TransactRepository {
    private static final String TAG = "kiev";
    private final TransactDao transactDao;
    private final CategoryDao categoryDao;
    private final WalletDao walletDao;
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
    }

    public Completable insertTransact(TransactModel transactModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    //insert transact
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
                    //delete transact
                    SyncState currentState = transactDao.getState(transactModel.getTran_id());
                    if (currentState == SyncState.synced)
                        deletedRowDao.insert(new DeletedRow(transactModel.getTran_id(), DeletedRow.Table.transact));
                    transactDao.delete(transactModel.getTran_id());

                    //after delete transact, wallet is then updated
                    updateWalletAmount(transactModel, Action.delete);
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

    public Single<List<TransactModel>> getAllByWalletId(Integer walletId){
        return transactDao
                .getAllTransactBelongTo(walletId)
                .map(transactWithCategories -> {
                    List<TransactModel> transactModels = new ArrayList<>();
                    transactWithCategories.forEach(transactWithCategory -> {
                        transactModels.add(toTransactModel(transactWithCategory.transact, transactWithCategory.category));
                    });
                    return transactModels;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Double> getWalletAmount(Integer walletId){
        return walletDao
                .getWalletAmount(walletId)
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
        transactModel.setCategoryModel(CategoryRepository.toCategoryModel(category));
        transactModel.setDate_time(transact.getDate_time());
        transactModel.setTran_amount(transact.getAmount());
        transactModel.setAuto_tran(transact.getAuto_tran());
        transactModel.setTran_description(transact.getDescription());
        return transactModel;
    }

    public static Item toItem(ItemModel itemModel, Integer billId, SyncState state){
        Item item = new Item();
        item.setId(item.getId());
        item.setBill_id(billId);
        item.setQuantity(itemModel.getQuantity());
        item.setItem_price(itemModel.getItem_price());
        item.setSyncState(state);
        return item;
    }
}
