package com.example.personalfinance.datalayer.local.dataclass;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.personalfinance.datalayer.local.entity.Budget;
import com.example.personalfinance.datalayer.local.entity.BudgetCategory;
import com.example.personalfinance.datalayer.local.entity.Category;

import java.util.List;

public class BudgetWithCategories {
    @Embedded
    public Budget budget;

    @Relation(
            parentColumn = "budget_id",
            entityColumn = "category_id",
            associateBy = @Junction(BudgetCategory.class)
    )
    public List<Category> categories;
}
