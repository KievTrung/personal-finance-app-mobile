package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entity.auxiliry.Token;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TokenDao {
    @Insert
    Completable addToken(Token token);

    @Query("delete from token")
    Completable deleteToken();

    @Query("select * from token limit 1")
    Single<String> getToken();

    default Completable setToken(Token token){
        return deleteToken().andThen(addToken(token));
    }
}
