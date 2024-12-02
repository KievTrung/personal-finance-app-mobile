package com.example.personalfinance.datalayer.local.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.enums.Language;
import com.example.personalfinance.datalayer.local.enums.SyncState;

@Entity
public class User {
    @PrimaryKey
    private Integer userId;
    private String userName;
    private String password;
    private String email;
    private Currency currency;
    private Language language;
    private SyncState syncState;

    public User(){}

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", currency=" + currency +
                ", language=" + language +
                ", syncState=" + syncState +
                '}';
    }
}
