package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.enums.Currency;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {
    @Query("select * from user limit 1")
    Single<User> getUser();

    @Insert
    long addUser(User user);

    @Query("delete from user")
    void deleteUser();

    @Update
    Completable updateUser(User user);

    @Query("update user set currency = :currency")
    Completable setCurrency(Currency currency);

    @Query("select currency from user limit 1")
    Single<Currency> getSingleCurrency();

    @Query("select currency from user limit 1")
    Currency getCurrency();
}
