package com.example.personalfinance.fragment.transaction.transaction;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.repositories.TransactRepository;
import com.example.personalfinance.fragment.transaction.transaction.model.TransactModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TransactionViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private TransactRepository transactRepository;
    private List<TransactModel> transacts;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static TransactModel tempTransact = new TransactModel();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactRepository = new TransactRepository(application.getApplicationContext());
        transacts = new ArrayList<>();
    }

    public List<TransactModel> getTransacts() {
        return transacts;
    }

    public Completable insertTransact(TransactModel transactModel){
        try {
            return transactRepository.insertTransact(transactModel);
        } catch (Exception e) {
            Log.d(TAG, "insertTransact: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Single<List<TransactModel>> getAll(Integer wallet_id){
        return transactRepository.getAllByWalletId(wallet_id).doOnSuccess(transactModels -> transacts = transactModels);
    }

    //need to check amount when inserting new transact of spending category
    public Single<Double> getWalletAmount(Integer wallet_id){
        return transactRepository.getWalletAmount(wallet_id);
    }

    public Completable deleteTransact(TransactModel transactModel){
        try {
            return transactRepository.deleteTransact(transactModel);
        } catch (Exception e) {
            Log.d(TAG, "deleteTransact: " + e.getMessage());
            return Completable.error(e);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AppLocalDatabase.closeDb();
        compositeDisposable.dispose();
    }
}
