package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.entity.Category;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Query("delete from category where category_id = :id")
    void deleteCategory(Integer id);

    @Query("select syncState from category where category_id = :id")
    SyncState getState(Integer id);

    @Query("update category set syncState = :state where category_id = :id")
    Completable setState(Integer id, SyncState state);

    @Query("select * from category where categoryType = :categoryType")
    Single<List<Category>> getCategories(CategoryType categoryType);

    @Query("select * from category where category_id = :id")
    Single<Category> getCategory(Integer id);

    @Query("select count(*) from transact where category_id = :id")
    Single<Integer> countTransactHaveCategory(Integer id);

    @Query("select count(*) from budgetcategory where category_id = :id")
    Single<Integer> countBudgetHaveCategory(Integer id);
}
