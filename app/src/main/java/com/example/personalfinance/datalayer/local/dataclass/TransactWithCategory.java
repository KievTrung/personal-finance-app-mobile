package com.example.personalfinance.datalayer.local.dataclass;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.personalfinance.datalayer.local.entity.Category;
import com.example.personalfinance.datalayer.local.entity.Transact;

public class TransactWithCategory {
    @Embedded
    public Transact transact;
    @Relation(
            parentColumn = "category_id",
            entityColumn = "category_id"
    )
    public Category category;
}
