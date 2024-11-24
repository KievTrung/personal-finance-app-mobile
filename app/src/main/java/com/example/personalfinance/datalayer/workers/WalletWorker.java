package com.example.personalfinance.datalayer.workers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;
import androidx.work.rxjava3.RxWorker;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UserDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.WalletService;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WalletWorker extends RxWorker {
    private static final String TAG = "Wallet worker";
    private UserDao userDao;
    private WalletDao walletDao;
    private WalletService walletService;

    public WalletWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        userDao = AppLocalDatabase.getInstance(appContext).getUserDao();
        walletDao = AppLocalDatabase.getInstance(appContext).getWalletDao();
        walletService = ApiServiceFactory.getWalletService();
    }

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public Single<Result> createWork() {
        Log.d(TAG, "createWork: invoked");
        //get all wallet
        Single<List<Wallet>> wallets = walletDao.getAllWallet();
        //get user id
        Single<Integer> userId = userDao.getUserId();
        //combine
        return Single.zip(wallets, userId, (walletList, id) -> {
            Log.d(TAG, "there are " + walletList.size() + " rows need to process");
            Log.d(TAG, "current user id: " + id);
            //set up queries for persisting task

            for (Wallet wallet : walletList){
                sendUpdateRequest(id, wallet)
                        .doOnError(throwable -> {
                            Log.e(TAG, "updated exception on wallet title: " + wallet.getWallet_title());
                            throwable.printStackTrace();
                        })
                        .onErrorComplete()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .blockingAwait();
            }
            return Result.success();
        });
    }

    private Completable sendUpdateRequest(Integer userId, Wallet wallet){
        /*
        //get wallet state
        SyncState syncState = wallet.getSyncState();

        Log.d(TAG, "process wallet: " + wallet.getWallet_title());
        Log.d(TAG, "sync state: " + syncState);

        //check for each wallet if it can be persist to remote
        switch(syncState){
            case not_sync_delete:
                return walletService
                        .deleteWallet(userId, wallet.getWallet_title())
                        .doOnSuccess(result -> Log.d(TAG, "sendUpdateRequest, delete : " + result))
                        .ignoreElement()
                        .andThen(walletDao.removeWallet(wallet.getWallet_title()))
                        .doOnComplete(() -> Log.d(TAG, "local, remove : " + wallet.getWallet_title()));
            case not_sync_insert:
                return walletService
                        .addWallet(userId, WalletRepository.toWalletRemote(wallet))
                        .doOnSuccess(walletRemote -> Log.d(TAG, "sendUpdateRequest, insertTransact : " + walletRemote))
                        .ignoreElement()
                        .andThen(walletDao.setState(wallet.getWallet_title(), SyncState.synced))
                        .andThen(walletDao.updateLastSyncTitle(wallet.getWallet_title()))
                        .doOnComplete(() -> Log.d(TAG, "local, insertTransact complete"));
            case not_sync_update:
                return walletService
                        .updateTitle(userId, wallet.getLast_sync_title(), wallet.getWallet_title())
                        .doOnSuccess(result -> Log.d(TAG, "sendUpdateRequest, update title: " + result))
                        .ignoreElement()
                        .andThen(walletDao.updateLastSyncTitle(wallet.getWallet_title()))
                        .doOnComplete(() -> Log.d(TAG, "sendUpdateRequest, update last sync title : " + wallet.getWallet_title()))
                        .andThen(walletService.updateAmount(userId, wallet.getWallet_title(), wallet.getWallet_amount()))
                        .doOnSuccess(result -> {
                            Log.d(TAG, "sendUpdateRequest, update amount : " + result);
                        })
                        .ignoreElement()
                        .andThen(walletService.updateDescription(userId, wallet.getWallet_title(), wallet.getWallet_description()))
                        .doOnSuccess(result -> Log.d(TAG, "sendUpdateRequest, update description : " + result))
                        .ignoreElement()
                        .andThen(walletDao.setState(wallet.getWallet_title(), SyncState.synced))
                        .doOnComplete(() -> Log.d(TAG, "sendUpdateRequest, update complete"));
            case no_sync_delete:
                return walletDao
                        .removeWallet(wallet.getWallet_title())
                        .doOnComplete(() -> Log.d(TAG, "sendUpdateRequest, remove : " + wallet.getWallet_title()));
            case synced: default:
                return Completable.complete();
        }
         */
        return Completable.complete();
    }
}
