package com.example.personalfinance;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.entity.User;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.wallet.repository.WalletRepository;
import com.example.personalfinance.fragment.wallet.model.WalletModel;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        walletRepository = new WalletRepository(application.getApplicationContext());
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public Completable setUser(User user){
        try {
            return userRepository.setUser(user);
        } catch (Exception e) {
            Log.d(TAG, "set user: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Single<Boolean> getNotifyPermission() {
        return userRepository.getNotifyPermission();
    }

    public Single<Integer> isUserExisted(){
        return userRepository.isUserExist();
    }

    public Completable switchCurrency(Currency currency){
        return userRepository.setCurrency(currency);
    }

    public Completable setNotify(boolean notify){
        return userRepository.setNotify(notify);
    }

    public Single<WalletModel> getUseWallet(){
        return walletRepository.getUseWallet();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        AppLocalDatabase.closeDb();
        compositeDisposable.dispose();
    }
}
