package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class UseWallet {
    @PrimaryKey
    @NonNull
    private Integer id;

    public UseWallet(Integer id) {
        this.id = id;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }
}
