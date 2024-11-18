package com.example.personalfinance.datalayer.repositories;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.personalfinance.datalayer.SyncStateManager;
import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UseWalletDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.entities.UseWallet;
import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.WalletService;
import com.example.personalfinance.datalayer.remote.models.WalletRemote;
import com.example.personalfinance.datalayer.workers.WalletWorker;
import com.example.personalfinance.datalayer.workers.WorkTag;
import com.example.personalfinance.fragment.transaction.models.WalletModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class WalletRepository {
    private static final String TAG = "kiev work";
    private final WalletDao walletDao;
    private final UseWalletDao useWalletDao;
    private final WalletService walletService;
    private static long PERIODIC_INTERVAL = 15l;
    private static long PERIODIC_FLEX = 14l;

    public WalletRepository(Context context){
        walletDao = AppLocalDatabase.getInstance(context).getWalletDao();
        useWalletDao = AppLocalDatabase.getInstance(context).getUseWalletDao();
        walletService = ApiServiceFactory.getWalletService();
    }

    public static void startWork(Context context){
        //set constraint for the work
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
        //create periodic work request
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

    public Completable useWallet(String wallet_title){
        //todo: set scheduler
        return useWalletDao.setUseWallet(new UseWallet(wallet_title));
    }

    public Completable addWallet(WalletModel walletModel){
        //todo: set scheduler
        //insert to local
        return walletDao.insertWallet(toWallet(walletModel)).andThen(useWallet(walletModel.getWallet_title()));
    }

    public Completable updateTitle(String old_title, String new_title){
        //update local
        return walletDao.getState(old_title)
                .flatMapCompletable(state -> walletDao.updateTitle(old_title
                        , new_title
                        , SyncStateManager.determineSyncState(SyncStateManager.Action.update, state)));
        //todo: set scheduler
    }

    public Completable updateAmount(String wallet_title, Double amount){
        //update local
        return walletDao.getState(wallet_title)
                .flatMapCompletable(state -> walletDao.updateAmount(wallet_title
                                            , amount
                                            , SyncStateManager.determineSyncState(SyncStateManager.Action.update, state)));
        //todo: set scheduler
    }

    public Completable updateDescription(String wallet_title, String description){
        //update local
        return walletDao.getState(wallet_title)
                .flatMapCompletable(state -> walletDao.updateDescription(wallet_title
                                                , description
                                                , SyncStateManager.determineSyncState(SyncStateManager.Action.update, state)));
        //todo: set scheduler
    }

    public Single<List<WalletModel>> getAllWallet(){
        //get use wallet
        Single<String> single = useWalletDao.getUseWallet().onErrorReturn(throwable -> {
            Log.d(TAG, "no use wallet yet");
            return "";
        });
        //get wallet list
        Single<List<WalletModel>> singles = walletDao.getAllWalletWithState(SyncState.not_delete_sync).map(wallets -> toWalletModels(wallets));
        //combine
        return Single.zip(single, singles, (s, walletModels) -> {
                    return walletModels
                            .stream()
                            .map(walletModel -> {
                                walletModel.setCurrent_use(s != "" && s.equals(walletModel.getWallet_title()) ? true : false);
                                return walletModel;
                            })
                            .collect(Collectors.toList());
                });
        //todo: set scheduler
    }

    public Completable deleteWallet(String title){
        return walletDao.getState(title)
                .flatMapCompletable(state -> walletDao.deleteWallet(title, SyncStateManager.determineSyncState(SyncStateManager.Action.delete, state)));
        //todo: set scheduler
    }

    public static Wallet toWallet(WalletModel walletModel){
        Wallet wallet = new Wallet();
        wallet.setWallet_title(walletModel.getWallet_title());
        wallet.setLast_sync_title(null);
        wallet.setWallet_amount(walletModel.getWallet_amount());
        wallet.setWallet_description(walletModel.getWallet_description());
        wallet.setSyncState(SyncStateManager.determineSyncState(SyncStateManager.Action.insert, null));
        return wallet;
    }

    public static WalletModel toWalletModel(Wallet wallet){
        WalletModel walletModel = new WalletModel();
        walletModel.setWallet_title(wallet.getWallet_title());
        walletModel.setWallet_amount(wallet.getWallet_amount());
        walletModel.setWallet_description(wallet.getWallet_description());
        walletModel.setCurrent_use(false);
        return walletModel;
    }

    public static WalletRemote toWalletRemote(Wallet wallet){
        WalletRemote walletRemote = new WalletRemote();
        walletRemote.setWallet_title(wallet.getWallet_title());
        walletRemote.setWallet_amount(wallet.getWallet_amount());
        walletRemote.setWallet_description(wallet.getWallet_description());
        return walletRemote;
    }

    private List<WalletModel> toWalletModels(List<Wallet> wallets){
        List<WalletModel> list = new ArrayList<>();
        for (Wallet wallet : wallets)
            list.add(toWalletModel(wallet));
        return list;
    }
}
