package com.example.personalfinance.datalayer.local.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.personalfinance.datalayer.local.enums.SyncState;

@Entity(primaryKeys = {"category_id", "budget_id"}
        , foreignKeys = {
        @ForeignKey(
                entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Budget.class,
                parentColumns = "budget_id",
                childColumns = "budget_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
    }
)
public class BudgetCategory {
    @NonNull
    public Integer category_id;
    @NonNull
    public Integer budget_id;
    @NonNull
    public SyncState syncState;

    public BudgetCategory(@NonNull Integer category_id, @NonNull Integer budget_id, @NonNull SyncState syncState) {
        this.category_id = category_id;
        this.budget_id = budget_id;
        this.syncState = syncState;
    }

    @NonNull
    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(@NonNull Integer category_id) {
        this.category_id = category_id;
    }

    @NonNull
    public Integer getBudget_id() {
        return budget_id;
    }

    public void setBudget_id(@NonNull Integer budget_id) {
        this.budget_id = budget_id;
    }

    @NonNull
    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(@NonNull SyncState syncState) {
        this.syncState = syncState;
    }
}
