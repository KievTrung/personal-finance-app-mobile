package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.util.Objects;

@Entity(indices = {@Index(value = {"wallet_title"}, unique = true)})
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @NonNull
    private String wallet_title;
    @NonNull
    private Double wallet_amount;
    private String wallet_description;
    @NonNull
    private SyncState syncState; //this will be set by sync manager

    public Wallet(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWallet_title() {
        return wallet_title;
    }

    public void setWallet_title(String wallet_title) {
        this.wallet_title = wallet_title;
    }

    public Double getWallet_amount() {
        return wallet_amount;
    }

    public void setWallet_amount(Double wallet_amount) {
        this.wallet_amount = wallet_amount;
    }

    public String getWallet_description() {
        return wallet_description;
    }

    public void setWallet_description(String wallet_description) {
        this.wallet_description = wallet_description;
    }

    @NonNull
    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(@NonNull SyncState syncState) {
        this.syncState = syncState;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", wallet_title='" + wallet_title + '\'' +
                ", wallet_amount=" + wallet_amount +
                ", wallet_description='" + wallet_description + '\'' +
                ", syncState=" + syncState +
                '}';
    }
}
