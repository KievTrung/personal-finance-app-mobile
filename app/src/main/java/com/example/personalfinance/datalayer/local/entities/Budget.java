package com.example.personalfinance.datalayer.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.Period;

import java.time.LocalDateTime;

@Entity
public class Budget {
    @PrimaryKey
    private String budget_title;
    private Long budget_amount;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private Period auto_budget;
    private String budget_description;
}
