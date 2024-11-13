package com.example.personalfinance.datalayer.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey
    private String item_name;
    private Integer quantity;
    private Long item_price;
}
