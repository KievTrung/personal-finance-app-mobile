package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entities.Transact;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TransactDao {
    @Insert
    Completable insertTransact(Transact transact);

    @Query("delete from transact where tran_id = :tran_id")
    Completable deleteTransactById(long tran_id);

    @Query("select * from transact")
    Single<List<Transact>> getAllTransact();

    @Query("select * from transact where tran_id = :tran_id")
    Single<Transact> getTransactById(long tran_id);
}
