package com.example.personalfinance.fragment.category.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.repositories.CategoryRepository;
import com.example.personalfinance.fragment.category.CategoryModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SpendingCategoryViewModel extends AndroidViewModel {
    private static final String TAG = "kiev";
    private List<CategoryModel> spendings;
    private CategoryRepository categoryRepository;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SpendingCategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application.getApplicationContext());
        spendings = new ArrayList<>();
    }

    public List<CategoryModel> getSpendings(){
        return spendings;
    }

    public Completable insert(CategoryModel categoryModel){
        Log.d(TAG, "insertTransact category: spending, " + categoryModel);
        try {
            return categoryRepository.insert(categoryModel);
        } catch (Exception e) {
            Log.d(TAG, "insert category: " + e.getMessage());
            return Completable.error(new RuntimeException(e));
        }
    }

    public Completable update(CategoryModel categoryModel){
        Log.d(TAG, "update category: spending, " + categoryModel);
        try {
            return categoryRepository.update(categoryModel);
        } catch (Exception e) {
            Log.d(TAG, "update category: " + e.getMessage());
            return Completable.error(new RuntimeException(e));
        }
    }

    public Completable delete(CategoryModel categoryModel){
        Log.d(TAG, "delete category : spending" + categoryModel);
        try {
            return categoryRepository.delete(categoryModel);
        } catch (Exception e) {
            Log.d(TAG, "delete category: " + e.getMessage());
            return Completable.error(new RuntimeException(e));
        }
    }

    public Single<List<CategoryModel>> fetchCategory(){
        Log.d(TAG, "fetch category: spending");
        return categoryRepository.getAll(CategoryType.spending).doOnSuccess(categoryModels -> spendings = categoryModels);
    }

    public Single<Integer> countTransact(Integer categoryId){
        return categoryRepository.countTransactHaveCategory(categoryId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
