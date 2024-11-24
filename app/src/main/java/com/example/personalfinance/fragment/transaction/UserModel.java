package com.example.personalfinance.fragment.transaction;

import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.enums.Language;

public class UserModel {
    private String userName;
    private String password;
    private String email;
    private Currency currency;
    private Language language;

    public UserModel(){}

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
        return "UserModel{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", currency=" + currency +
                ", language=" + language +
                '}';
    }
}
