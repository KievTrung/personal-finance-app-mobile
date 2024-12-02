package com.example.personalfinance.fragment.account;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class AccountViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private UserRepository userRepository;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public AccountViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public Single<User> getUser(){
        return userRepository.getUser();
    }

    public Completable setUser(User user){
        try {
            return userRepository.setUser(user);
        } catch (Exception e) {
            Log.d(TAG, "set user: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Completable switchCurrency(Currency currency){
        return userRepository.setCurrency(currency);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AppLocalDatabase.closeDb();
        compositeDisposable.dispose();
    }
}
