package com.example.personalfinance.fragment.transaction.viewmodel;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.transaction.repository.TransactRepository;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.transaction.model.Filter;
import com.example.personalfinance.fragment.transaction.model.ItemModel;
import com.example.personalfinance.fragment.transaction.model.TransactModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TransactionViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private TransactRepository transactRepository;
    private UserRepository userRepository;
    private List<TransactModel> transacts;

    public enum Action{ update, insert }
    public static TransactModel tempTransact = new TransactModel();
    public static Action action;
    public static boolean requestAddCategoryToFilter = false;
    public static Currency currency;
    public Filter filter;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactRepository = new TransactRepository(application.getApplicationContext());
        userRepository = new UserRepository(application.getApplicationContext());
        filter = new Filter();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<List<MainActivity.NotifyObject>> postNotificationIfExceededBudget(@NonNull TransactModel transactModel){
        return transactRepository.postNotificationIfExceededBudget(transactModel);
    }

    public Single<List<ItemModel>> getAllItem(Integer billId){
        return transactRepository.getAllItemBelongTo(billId);
    }

    public Completable updateTransact(TransactModel transactModel){
        try {
            return transactRepository.updateTransact(transactModel);
        } catch (Exception e) {
            Log.d(TAG, "update transact: " + e.getMessage());
            return Completable.error(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<List<TransactModel>> fetchAll(Integer wallet_id, Filter filter){
        return transactRepository.getAllByWalletId(wallet_id, filter).doOnSuccess(transactModels -> transacts = transactModels);
    }

    public Single<TransactModel> get(Integer id){
        return transactRepository.getTransact(id);
    }

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

    public Single<Currency> getCurrency(){
        return userRepository.getCurrency();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AppLocalDatabase.closeDb();
        compositeDisposable.dispose();
    }
}
