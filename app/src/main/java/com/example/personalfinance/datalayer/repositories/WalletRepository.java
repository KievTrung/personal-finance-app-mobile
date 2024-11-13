package com.example.personalfinance.datalayer.repositories;

import android.content.Context;
import android.util.Log;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UseWalletDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.entities.UseWallet;
import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.local.enums.RemoveState;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.WalletService;
import com.example.personalfinance.datalayer.remote.models.WalletRemote;
import com.example.personalfinance.fragment.transaction.models.WalletModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WalletRepository {
    private static final String TAG = "kiev";
    private final WalletDao walletDao;
    private final UseWalletDao useWalletDao;
    private final WalletService walletService;

    public WalletRepository(Context context){
        walletDao = AppLocalDatabase.getInstance(context).getWalletDao();
        useWalletDao = AppLocalDatabase.getInstance(context).getUseWalletDao();
        walletService = ApiServiceFactory.getWalletService();
    }

    public Completable useWallet(String wallet_title){
        return useWalletDao.setUseWallet(new UseWallet(wallet_title))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable addWallet(WalletModel walletModel){
        //insert to local
        return walletDao.insertWallet(toWallet(walletModel)).andThen(useWallet(walletModel.getWallet_title()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateTitle(String old_title, String new_title ){
        //update local
        return walletDao.udpateTitle(old_title, new_title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateAmount(String wallet_title, Double amount){
        //update local
        return walletDao.udpateAmount(wallet_title, amount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateDescription(String wallet_title, String description){
        //update local
        return walletDao.updateSyncState(wallet_title, description)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<WalletModel>> getAllWallet(){
        //get use wallet
        Single<String> single = useWalletDao.getUseWallet().onErrorReturn(throwable -> {
            Log.d(TAG, "no use wallet yet");
            return "";
        });
        //get wallet list
        Single<List<WalletModel>> singles = walletDao.getAllWallets().map(wallets -> toWalletModels(wallets));
        //combine
        return Single.zip(single, singles, (s, walletModels) -> {
                    return walletModels
                            .stream()
                            .map(walletModel -> {
                                walletModel.setCurrent_use(s != "" && s.equals(walletModel.getWallet_title()) ? true : false);
                                return walletModel;
                            })
                            .collect(Collectors.toList());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteWallet(String title){
        return walletDao.deleteWallet(title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Wallet toWallet(WalletRemote walletRemote){
        Wallet wallet = new Wallet();
        wallet.setWallet_title(walletRemote.getWallet_title());
        wallet.setWallet_amount(walletRemote.getWallet_amount());
        wallet.setWallet_description(walletRemote.getWallet_description());
        return wallet;
    }

    private Wallet toWallet(WalletModel walletModel){
        Wallet wallet = new Wallet();
        wallet.setWallet_title(walletModel.getWallet_title());
        wallet.setWallet_amount(walletModel.getWallet_amount());
        wallet.setWallet_description(walletModel.getWallet_description());
        wallet.setSyncState(SyncState.not_sync);
        wallet.setRemoveState(RemoveState.active);
        return wallet;
    }

    private WalletModel toWalletModel(Wallet wallet){
        WalletModel walletModel = new WalletModel();
        walletModel.setWallet_title(wallet.getWallet_title());
        walletModel.setWallet_amount(wallet.getWallet_amount());
        walletModel.setWallet_description(wallet.getWallet_description());
        walletModel.setCurrent_use(false);
        return walletModel;
    }

    private WalletRemote toWalletRemote(WalletModel walletModel){
        WalletRemote walletRemote = new WalletRemote();
        walletRemote.setWallet_title(walletModel.getWallet_title());
        walletRemote.setWallet_amount(walletModel.getWallet_amount());
        walletRemote.setWallet_description(walletModel.getWallet_description());
        return walletRemote;
    }

    private List<WalletModel> toWalletModels(List<Wallet> wallets){
        List<WalletModel> list = new ArrayList<>();
        for (Wallet wallet : wallets)
            list.add(toWalletModel(wallet));
        return list;
    }
}
