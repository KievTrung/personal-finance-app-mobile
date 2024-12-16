package com.example.personalfinance.fragment.setting.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.entity.User;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.repository.UserRepository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SettingViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private final UserRepository userRepository;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public boolean allowNotify;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public Single<User> getUser(){
        return userRepository.getUser();
    }


    public Completable switchCurrency(Currency currency){
        return userRepository.setCurrency(currency);
    }

    public Single<Boolean> getNotifyPer(){
        return userRepository.getNotifyPermission().doOnSuccess(aBoolean -> allowNotify = aBoolean);
    }

    public Completable setNotify(boolean notify){
        return userRepository.setNotify(notify);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AppLocalDatabase.closeDb();
        compositeDisposable.dispose();
    }
}
