package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.dataclass.TransactWithCategory;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.fragment.transaction.transaction.model.Filter;

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

    @Query("select * from transact where id = :id")
    Single<TransactWithCategory> getTransact(Integer id);

    @Query("select * from transact where id = :tran_id")
    Single<Transact> getTransactById(Integer tran_id);

    @Query("delete from transact where id = :id")
    void deleteTransact(Integer id);

    @Query("select syncState from transact where id = :id")
    SyncState getState(Integer id);

}
