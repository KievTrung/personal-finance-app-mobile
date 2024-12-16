package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.dataclass.BudgetWithCategories;
import com.example.personalfinance.datalayer.local.dataclass.TransactWithCategory;
import com.example.personalfinance.datalayer.local.entity.Budget;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface BudgetDao {
    @Insert
    long insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Transaction
    @Query("select * from budget")
    Single<List<BudgetWithCategories>> getAllBudget();

    @Query("select sum(t.amount) from budget as b " +
            "inner join budgetcategory as bc on b.budget_id = bc.budget_id " +
            "inner join transact as t on bc.category_id = t.category_id " +
            "where b.budget_id = :budgetId and t.date_time between b.start_date and b.end_date")
    Double totalTransactsAmount(Integer budgetId);

    @Query("select * from budget as b " +
            "inner join budgetcategory as bc on b.budget_id = bc.budget_id " +
            "inner join transact as t on bc.category_id = t.category_id " +
            "where b.budget_id = :budgetId and date_time between b.start_date and b.end_date")
    Single<List<TransactWithCategory>> getAllTransacts(Integer budgetId);

    @Query("delete from budget where budget_id = :budgetId")
    void deleteBudget(Integer budgetId);

    @Query("select syncState from budget where budget_id = :budgetId")
    SyncState getState(Integer budgetId);

    @Query("select budget_id from budget where budget_title = :title")
    Single<Integer> getBudgetId(String title);

    @Query("select * from budget where budget_id = :budgetId")
    Single<BudgetWithCategories> getBudget(Integer budgetId);
}
