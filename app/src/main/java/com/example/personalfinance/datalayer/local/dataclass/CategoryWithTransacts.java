package com.example.personalfinance.datalayer.local.dataclass;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.personalfinance.datalayer.local.entity.Category;
import com.example.personalfinance.datalayer.local.entity.Transact;

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
