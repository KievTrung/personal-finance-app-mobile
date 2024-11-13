package com.example.personalfinance.datalayer.local.relationships;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.entities.Transact;

import java.util.List;

public class CategoryWithTransacts {
    @Embedded
    public Category category;

    @Relation(
            parentColumn = "category_id",
            entityColumn = "category_id"
    )
    public List<Transact> transactList;
}
