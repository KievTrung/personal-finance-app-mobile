package com.example.personalfinance.fragment.budget.viewmodel;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.budget.repository.BudgetRepository;
import com.example.personalfinance.fragment.notify.repository.NotifyRepository;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.budget.model.BudgetModel;
import com.example.personalfinance.fragment.transaction.model.Filter;
import com.example.personalfinance.fragment.transaction.model.TransactModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BudgetViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final NotifyRepository notifyRepository;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<BudgetModel> budgets;

    public enum Action{ insert, update, filter }
    public BudgetModel budgetModel;
    public static boolean requestAddCategory = false;
    public static Action action;
    public static Currency currency;
    public Filter filter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public BudgetViewModel(@NonNull Application application) {
        super(application);
        budgetRepository = new BudgetRepository(application.getApplicationContext());
        userRepository = new UserRepository(application.getApplicationContext());
        notifyRepository = new NotifyRepository(application.getApplicationContext());
        budgets = new ArrayList<>();
        filter = new Filter();
    }

    public List<BudgetModel> getBudgets() {
        return budgets;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<BudgetModel> getBudget(Integer budgetId){
        return budgetRepository.getBudget(budgetId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<List<BudgetModel>> fetch(Filter filter){
        return budgetRepository.getAllBudget(filter).doOnSuccess(budgetModels -> budgets = budgetModels);
    }

    public Completable insert(BudgetModel budget){
        try {
            return budgetRepository.insertBudget(budget);
        } catch (Exception e) {
            Log.d(TAG, "insert budget: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Completable insertCategory(Integer budgetId, Integer categoryId){
        try {
            return budgetRepository.addCategory(budgetId, categoryId);
        } catch (Exception e) {
            Log.d(TAG, "insert budget category: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Completable update(BudgetModel budgetModel){
        try {
            return budgetRepository.updateBudget(budgetModel);
        } catch (Exception e) {
            Log.d(TAG, "update budget: " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Completable hasRead(Integer notify_id){
        return notifyRepository.hasRead(notify_id);
    }

    public Completable delete(BudgetModel budget){
        try {
            return budgetRepository.deleteBudget(budget);
        } catch (Exception e) {
            Log.d(TAG, "delete: budget " + e.getMessage());
            return Completable.error(e);
        }
    }

    public Completable deleteCategory(Integer budgetId, Integer categoryId){
        try {
            return budgetRepository.deleteCategory(budgetId, categoryId);
        } catch (Exception e) {
            Log.d(TAG, "delete: budget category" + e.getMessage());
            return Completable.error(e);
        }
    }

    public Single<List<TransactModel>> getAllTransact(Integer budgetId){
        return budgetRepository.getAllTransacts(budgetId);
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
