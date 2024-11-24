package com.example.personalfinance.datalayer.local.dataclass;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.entities.Transact;

public class TransactWithCategory {
    @Embedded
    public Transact transact;
    @Relation(
            parentColumn = "category_id",
            entityColumn = "id"
    )
    public Category category;
}
