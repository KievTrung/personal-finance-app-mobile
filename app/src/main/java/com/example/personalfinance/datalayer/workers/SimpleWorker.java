package com.example.personalfinance.datalayer.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;
import androidx.work.rxjava3.RxWorker;

import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.WalletService;
import com.example.personalfinance.datalayer.remote.models.WalletRemote;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SimpleWorker extends RxWorker {
    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public SimpleWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        walletService = ApiServiceFactory.getWalletService();
    }

    WalletService walletService;

    @NonNull
    @Override
    public Single<Result> createWork() {
        return walletService.getWallets(1).map(walletRemotes -> {
            Log.d("kiev", "this has been calleed");
            for(WalletRemote walletRemote : walletRemotes)
                Log.d("kiev", "createWork: " + walletRemote.getWallet_title());
            return Result.success();
        })
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return Result.failure();
                });
    }
}
