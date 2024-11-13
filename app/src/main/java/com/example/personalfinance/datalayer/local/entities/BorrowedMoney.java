package com.example.personalfinance.datalayer.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.InterestType;
import com.example.personalfinance.datalayer.local.enums.Period;
import com.example.personalfinance.datalayer.local.enums.State;

import java.time.LocalDateTime;

@Entity
public class BorrowedMoney {
    @PrimaryKey
    private String borrow_title;
    private Long borrow_amount;
    private Float interest_rate;
    private Period period;
    private InterestType interestType;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private State state;
    private String borrow_description;
}
