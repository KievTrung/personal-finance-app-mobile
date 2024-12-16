package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entity.User;
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

    @Query("select count(*) from user limit 1")
    Single<Integer> countUser();

    @Query("update user set currency = :currency")
    Completable setCurrency(Currency currency);

    @Query("select currency from user limit 1")
    Single<Currency> getSingleCurrency();

    @Query("select currency from user limit 1")
    Currency getCurrency();

    @Query("select notifyPermission from user limit 1")
    Single<Boolean> getNotifyPermission();

    @Query("update user set notifyPermission = :notify")
    Completable setNotifyPermission(boolean notify);
}
