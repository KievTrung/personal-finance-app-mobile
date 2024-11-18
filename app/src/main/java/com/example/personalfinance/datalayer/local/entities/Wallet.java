package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.util.Objects;

@Entity
public class Wallet {
    @PrimaryKey
    @NonNull
    private String wallet_title;
    private String last_sync_title;
    @NonNull
    private Double wallet_amount;
    private String wallet_description;
    @NonNull
    private SyncState syncState;

    public Wallet(){}

    public String getLast_sync_title() {
        return last_sync_title;
    }

    public void setLast_sync_title(String last_sync_title) {
        this.last_sync_title = last_sync_title;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(wallet_title, wallet.wallet_title) && Objects.equals(wallet_amount, wallet.wallet_amount) && Objects.equals(wallet_description, wallet.wallet_description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wallet_title, wallet_amount, wallet_description);
    }

}
