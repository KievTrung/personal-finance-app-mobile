package com.example.personalfinance;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;
import com.example.personalfinance.datalayer.local.repositories.WalletRepository;
import com.example.personalfinance.fragment.transaction.wallet.WalletModel;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private WalletRepository walletRepository;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        walletRepository = new WalletRepository(application.getApplicationContext());
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
