package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Wallet.class,
                parentColumns = "wallet_title",
                childColumns = "wallet_title",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
)
public class UseWallet {
    @PrimaryKey
    @NonNull
    private String wallet_title;

    public UseWallet() {
    }

    public UseWallet(String wallet_title){
        this.wallet_title = wallet_title;
    }

    public String getWallet_title() {
        return wallet_title;
    }

    public void setWallet_title(String wallet_title) {
        this.wallet_title = wallet_title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UseWallet useWallet = (UseWallet) o;
        return Objects.equals(wallet_title, useWallet.wallet_title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(wallet_title);
    }
}
