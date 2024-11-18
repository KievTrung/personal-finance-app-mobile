package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entities.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {
    @Query("select * from user limit 1")
    Single<User> getUser();

    @Query("select userId from user limit 1")
    Single<Integer> getUserId();

    @Insert
    Completable addUser(User user);
}
