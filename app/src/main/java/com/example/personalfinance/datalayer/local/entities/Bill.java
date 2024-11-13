package com.example.personalfinance.datalayer.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.Period;

import java.time.LocalDateTime;

@Entity
public class Bill {
    @PrimaryKey(autoGenerate = true)
    private Integer bill_id;
    private String bill_title;
    private LocalDateTime date_time;
    private Period auto_bill;
    private Long bill_amount;
    private String bill_description;
}
