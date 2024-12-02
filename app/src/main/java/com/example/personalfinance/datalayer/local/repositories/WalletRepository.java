package com.example.personalfinance.datalayer.local.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.personalfinance.datalayer.local.daos.UserDao;
import com.example.personalfinance.datalayer.local.daos.auxiliry.DeletedRowDao;
import com.example.personalfinance.datalayer.local.entities.auxiliry.DeletedRow;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.workers.SyncStateManager;
import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UseWalletDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.entities.UseWallet;
import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.WalletService;
import com.example.personalfinance.datalayer.workers.WalletWorker;
import com.example.personalfinance.datalayer.workers.WorkTag;
import com.example.personalfinance.fragment.transaction.wallet.WalletModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WalletRepository {
    private static final String TAG = "kiev";
    private final WalletDao walletDao;
    private final UseWalletDao useWalletDao;
    private final UserDao userDao;
    private final DeletedRowDao deletedRowDao;

    public WalletRepository(Context context){
        AppLocalDatabase app = AppLocalDatabase.getInstance(context);
        walletDao = app.getWalletDao();
        useWalletDao = app.getUseWalletDao();
        deletedRowDao = app.getDeletedRowDao();
        userDao = app.getUserDao();
    }

    public static void startWork(Context context){
        //set constraint for the work
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
        //create periodic work request
        long PERIODIC_FLEX = 14l;
        long PERIODIC_INTERVAL = 15l;
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                .Builder(WalletWorker.class,
                PERIODIC_INTERVAL, TimeUnit.MINUTES,
                PERIODIC_FLEX, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .addTag(WorkTag.persistent.toString())
                .build();
        //submit unique work to the system
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "persistUniqueWallet",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
        );
        Log.d(TAG, "start wallet wor");
    }

    public Completable useWallet(Integer id) throws Exception {
        return AppLocalDatabase.executeTransaction(() -> {
                    useWalletDao.deleteUseWallet();
                    useWalletDao.insertUseWallet(new UseWallet(id));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insert(WalletModel walletModel) throws Exception {
        return AppLocalDatabase.executeTransaction(() -> {
            walletModel.setWallet_amount(UserRepository.backCurrency(walletModel.getWallet_amount(), userDao.getCurrency()));
            Log.d(TAG, "insert: " + walletModel.getWallet_amount());
            Log.d(TAG, "insert: " + UserRepository.toCurrency(walletModel.getWallet_amount(), Currency.vnd));
            long id = walletDao.insertWallet(toWallet(walletModel, SyncStateManager.Action.insert, null));
            useWalletDao.deleteUseWallet();
            useWalletDao.insertUseWallet(new UseWallet((int)id));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(WalletModel walletModel) throws Exception {
        return AppLocalDatabase.executeTransaction(() -> {
                    SyncState state = walletDao.getState(walletModel.getId());
                    walletModel.setWallet_amount(UserRepository.backCurrency(walletModel.getWallet_amount(), userDao.getCurrency()));
                    walletDao.updateWallet(toWallet(walletModel, SyncStateManager.Action.update, state));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<WalletModel>> getAll(){
        //get use wallet
        Single<Integer> single = useWalletDao.getUseWalletObservable().onErrorReturn(throwable -> {
            Log.d(TAG, "no use wallet yet");
            return -1;
        });
        //get wallet list
        Single<List<WalletModel>> singles = walletDao
                .getAllWallet()
                .map(WalletRepository::toWalletModels);
        //combine
        return Single.zip(single, singles, (id, walletModels) ->
                    walletModels
                            .stream()
                            .map(walletModel -> {
                                walletModel.setCurrent_use((id != -1 && id == walletModel.getId()) ? true : false);
                                return walletModel;
                            })
                            .collect(Collectors.toList()))
                //note: if test then override these schedulers
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<WalletModel> getUseWallet(){
        return useWalletDao
                .getUseWalletObservable()
                .flatMap(walletDao::getWalletById)
                .onErrorReturn(throwable -> {
                    Wallet wallet = new Wallet();
                    wallet.setId(-1);
                    return wallet;
                })
                .flatMap(wallet -> Single.just(toWalletModel(wallet)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressLint("CheckResult")
    public Completable delete(WalletModel walletModel) throws Exception {
        return AppLocalDatabase.executeTransaction(() -> {
            long id = useWalletDao.getUseWallet();
            if (id == walletModel.getId())
                useWalletDao.deleteUseWallet();

            SyncState state = walletDao.getState(walletModel.getId());
            if (state == SyncState.synced)
                deletedRowDao.insert(new DeletedRow(walletModel.getId(), DeletedRow.Table.wallet));
            walletDao.deleteWallet(walletModel.getId());
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> countTransactInWallet(Integer walletId){
        return walletDao
                .countTransactInWallet(walletId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Wallet toWallet(WalletModel walletModel, SyncStateManager.Action action, SyncState currentState){
        Wallet wallet = new Wallet();
        wallet.setId(walletModel.getId());
        wallet.setWallet_title(walletModel.getWallet_title());
        wallet.setWallet_amount(walletModel.getWallet_amount());
        wallet.setWallet_description(walletModel.getWallet_description());
        wallet.setSyncState(SyncStateManager.determineSyncState(action, currentState));
        return wallet;
    }

    public static WalletModel toWalletModel(Wallet wallet){
        WalletModel walletModel = new WalletModel();
        walletModel.setId(wallet.getId());
        walletModel.setWallet_title(wallet.getWallet_title());
        walletModel.setWallet_amount(wallet.getWallet_amount());
        walletModel.setWallet_description(wallet.getWallet_description());
        walletModel.setCurrent_use(false);
        return walletModel;
    }

    private static List<WalletModel> toWalletModels(List<Wallet> wallets){
        List<WalletModel> list = new ArrayList<>();
        for (Wallet wallet : wallets)
            list.add(toWalletModel(wallet));
        return list;
    }
}
