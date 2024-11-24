package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.dataclass.TransactWithCategory;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TransactDao {
    @Insert
    long insertTransact(Transact transact);

    @Update
    Completable update(Transact transact);

    @Query("select * from transact where wallet_id = :walletId")
    Single<List<TransactWithCategory>> getAllTransactBelongTo(Integer walletId);

    @Query("select * from transact where id = :tran_id")
    Single<Transact> getTransactById(Integer tran_id);

    @Query("delete from transact where id = :id")
    void delete(Integer id);

    @Query("select syncState from transact where id = :id")
    SyncState getState(Integer id);
}
