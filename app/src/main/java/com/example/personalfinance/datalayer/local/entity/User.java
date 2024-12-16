package com.example.personalfinance.datalayer.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.Currency;

@Entity
public class User {
    @PrimaryKey
    private Integer userId;
    private Currency currency;
    private boolean notifyPermission;

    public User(){}

    public User(Currency currency, boolean allowNotify){
        this.currency = currency;
        notifyPermission = allowNotify;
    }

    public Boolean getNotifyPermission() {
        return notifyPermission;
    }

    public void setNotifyPermission(Boolean notifyPermission) {
        this.notifyPermission = notifyPermission;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", currency=" + currency +
                '}';
    }
}
