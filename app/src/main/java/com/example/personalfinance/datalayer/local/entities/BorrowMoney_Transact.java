package com.example.personalfinance.datalayer.local.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"borrow_id", "tran_id"})
public class BorrowMoney_Transact {
    private Integer borrow_id;
    private Integer tran_id;
}
