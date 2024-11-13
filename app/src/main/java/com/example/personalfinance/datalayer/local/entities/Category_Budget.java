package com.example.personalfinance.datalayer.local.entities;

import androidx.room.Entity;

@Entity(primaryKeys={"budget_id, category_id"})
public class Category_Budget{
    private Integer budget_id;
    private String category_id;
}
