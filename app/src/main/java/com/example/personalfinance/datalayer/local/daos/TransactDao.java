package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.dataclass.TransactWithCategory;
import com.example.personalfinance.datalayer.local.entity.Transact;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.time.LocalDateTime;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface TransactDao {
    @Insert
    long insertTransact(Transact transact);

    @Update
    void updateTransact(Transact transact);

    @Query("select * from transact where wallet_id = :walletId and date_time between :from_ and :to")
    Single<List<TransactWithCategory>> getAllTransactBelongTo(Integer walletId, LocalDateTime from_, LocalDateTime to);

    @Query("select * from transact where transact_id = :id")
    Single<TransactWithCategory> getTransact(Integer id);

    @Query("select * from transact order by date_time desc limit 1")
    Transact getLatest();

    @Query("delete from transact where transact_id = :id")
    void deleteTransact(Integer id);

    @Query("select syncState from transact where transact_id = :id")
    SyncState getState(Integer id);


}
