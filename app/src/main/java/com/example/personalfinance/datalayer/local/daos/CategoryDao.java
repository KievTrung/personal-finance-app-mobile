package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.relationships.CategoryWithTransacts;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CategoryDao {
    @Insert
    Completable insertCategory(Category category);

    @Update
    Completable updateCategory(Category category);

    @Query("delete from category where category_id = :category_id")
    Completable deleteCategoryById(String category_id);

    @Query("select * from category")
    Single<List<Category>> getCategories();

    @Query("select * from category")
    Single<List<CategoryWithTransacts>> getCategoryWithTransacts();
}
