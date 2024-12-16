package com.example.personalfinance.fragment.notify.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.entity.Notify;
import com.example.personalfinance.fragment.budget.repository.BudgetRepository;
import com.example.personalfinance.fragment.notify.repository.NotifyRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class NotifyViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private final NotifyRepository notifyRepository;
    private final BudgetRepository budgetRepository;
    private List<Notify> notifies = new ArrayList<>();
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public NotifyViewModel(@NonNull Application application) {
        super(application);
        notifyRepository = new NotifyRepository(application.getApplicationContext());
        budgetRepository = new BudgetRepository(application.getApplicationContext());
    }

    public List<Notify> getNotifies() {
        return notifies;
    }

    public Single<List<Notify>> getAll(){
        return notifyRepository.getAll().doOnSuccess(notifies1 -> notifies = notifies1);
    }

    public Single<Integer> getBudgetId(String title){
        return budgetRepository.getBudgetId(title);
    }

    public Completable hasRead(Integer notify_id){
        return notifyRepository.hasRead(notify_id);
    }

    public Completable delete(Notify notify){
        try {
            return notifyRepository.delete(notify);
        } catch (Exception e) {
            Log.d(TAG, "delete: notify " + e.getMessage());
            return Completable.error(e);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
