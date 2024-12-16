package com.example.personalfinance.fragment.wallet.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.wallet.repository.WalletRepository;
import com.example.personalfinance.fragment.wallet.model.WalletModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class WalletViewModel extends AndroidViewModel{
    private static final String TAG = "kiev";
    private List<WalletModel> wallets;
    public CompositeDisposable compositeDisposable;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public enum WalletAction { create, update, delete, update_transaction}

    public static WalletAction walletAction = null;
    public static Integer position = null;
    public static Completable completable = null;
    public static WalletModel useWallet = null;
    public static Currency currency;
    public static WalletModel wallet;

    public WalletViewModel(Application application){
        super(application);
        wallets = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        walletRepository = new WalletRepository(getApplication().getApplicationContext());
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public boolean isWalletUse(){
        for (WalletModel walletModel : wallets) if (walletModel.getCurrent_use()) return true;
        return false;
    }

    //this function is design for transaciton fragment
    public Single<WalletModel> getUseWallet(){
        return walletRepository.getUseWallet().doOnSuccess(walletModel1 -> {
            useWallet = walletModel1;
        });
    }

    public List<WalletModel> getWallets(){
        return wallets;
    }

    public Completable useWallet(int position) {
        Log.d(TAG, "useWallet");
        try {
            return walletRepository.useWallet(wallets.get(position).getId()).doOnComplete(() -> updateUseWallet(position));
        } catch (Exception e) {
            Log.d(TAG, "useWallet: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Single<List<WalletModel>> fetchWallets(){
        return walletRepository.getAll().doOnSuccess(walletModels -> wallets = walletModels);
    }

    private void updateUseWallet(int position){
        for (int i=0; i<wallets.size(); i++)
        {
            WalletModel walletModel = wallets.get(i);
            if (i == position)
                walletModel.setCurrent_use(true);
            else
                walletModel.setCurrent_use(false);
        }
        Log.d(TAG, "updateUseWallet");
    }

    public Completable add(WalletModel walletModel){
        Log.d(TAG, "addWallet");
        try {
            return walletRepository.insert(walletModel);
        } catch (Exception e) {
            Log.d(TAG, "add wallet: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Completable update(WalletModel newWalletModel){
        Log.d(TAG, "updateWallet: title");
        try {
            return walletRepository.update(newWalletModel);
        } catch (Exception e) {
            Log.d(TAG, "update wallet: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public WalletModel get(int position){
        return wallets.get(position);
    }

    public Completable remove(WalletModel walletModel){
        Log.d(TAG, "removeWallet");
        try {
            return walletRepository.delete(walletModel);
        } catch (Exception e) {
            Log.d(TAG, "remove wallet: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Single<Integer> countTransactWithWallet(Integer id){
        return walletRepository.countTransactInWallet(id);
    }

    public Single<Currency> getCurrency(){
        return userRepository.getCurrency();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
