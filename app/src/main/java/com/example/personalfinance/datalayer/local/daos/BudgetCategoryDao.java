package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entity.BudgetCategory;
import com.example.personalfinance.datalayer.local.enums.SyncState;

@Dao
public interface BudgetCategoryDao {
    @Insert
    void insert(BudgetCategory budgetCategory);

    @Query("delete from budgetcategory where budget_id = :budgetId and category_id = :categoryId")
    void delete(Integer budgetId, Integer categoryId);

    @Query("select syncState from budgetcategory where budget_id = :budgetId and category_id = :categoryId")
    SyncState getState(Integer budgetId, Integer categoryId);

}
