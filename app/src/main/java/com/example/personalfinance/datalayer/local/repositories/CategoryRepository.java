package com.example.personalfinance.datalayer.local.repositories;

import android.content.Context;

import com.example.personalfinance.datalayer.local.daos.auxiliry.DeletedRowDao;
import com.example.personalfinance.datalayer.local.entities.auxiliry.DeletedRow;
import com.example.personalfinance.datalayer.workers.SyncStateManager;
import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.CategoryDao;
import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.fragment.category.CategoryModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final DeletedRowDao deletedRowDao;

    public CategoryRepository(Context context){
        categoryDao = AppLocalDatabase.getInstance(context).getCategoryDao();
        deletedRowDao = AppLocalDatabase.getInstance(context).getDeletedRowDao();
    }

    public void checkContraint(String title) throws RuntimeException {
        String s = title.trim();
        try{
            if (title.isEmpty())
                throw new RuntimeException("Invalid category, must not empty, please try again");
            Integer.parseInt(s);
            /*error*/
            throw new RuntimeException("Invalid category, start with word, please try again");
        }catch (NumberFormatException e){
            /*pass*/
        }catch (RuntimeException e){
            throw e;
        }
    }

    public Completable insert(CategoryModel category) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    checkContraint(category.getName());
                    categoryDao.insertCategory(toCategory(category, SyncStateManager.Action.insert, null));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(CategoryModel categoryModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    checkContraint(categoryModel.getName());
                    SyncState currentState = categoryDao.getState(categoryModel.getId());
                    categoryDao.updateCategory(toCategory(categoryModel, SyncStateManager.Action.update, currentState));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete(CategoryModel categoryModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    SyncState currentState = categoryDao.getState(categoryModel.getId());
                    if (currentState == SyncState.synced)
                        deletedRowDao.insert(new DeletedRow(categoryModel.getId(), DeletedRow.Table.category));
                    categoryDao.deleteCategory(categoryModel.getId());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<CategoryModel>> getAll(CategoryType categoryType){
        return categoryDao
                .getCategories(categoryType)
                .map(CategoryRepository::toCategoryModels)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> countTransactHaveCategory(Integer categoryId){
        return categoryDao
                .countTransactHaveCategory(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static CategoryModel toCategoryModel(Category category){
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setName(category.getCategoryName());
        categoryModel.setId(category.getId());
        categoryModel.setCategoryType(category.getCategoryType());
        return categoryModel;
    }

    public static Category toCategory(CategoryModel categoryModel, SyncStateManager.Action action, SyncState currentState){
        Category category = new Category();
        category.setId(categoryModel.getId());
        category.setCategoryName(categoryModel.getName());
        category.setCategoryType(categoryModel.getCategoryType());
        category.setSyncState(SyncStateManager.determineSyncState(action, currentState));
        category.setLast_sync_name(null);
        return category;
    }

    public static List<CategoryModel> toCategoryModels(List<Category> categories){
        List<CategoryModel> categoryModels = new ArrayList<>();
        for (Category category : categories)
            categoryModels.add(toCategoryModel(category));
        return categoryModels;
    }
}
