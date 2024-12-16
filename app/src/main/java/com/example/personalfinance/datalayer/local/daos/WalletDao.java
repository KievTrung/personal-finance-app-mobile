package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.entity.Wallet;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface WalletDao {
    @Insert
    long insertWallet(Wallet wallet);

    @Update
    void updateWallet(Wallet wallet);

    @Query("delete from wallet where id = :id")
    void deleteWallet(Integer id);

    @Query("select syncState from wallet where id = :id")
    SyncState getState(Integer id);

    @Query("update wallet set syncState = :state where id = :id")
    void setState(Integer id, SyncState state);

    @Query("select * from wallet")
    Single<List<Wallet>> getAllWallet();

    @Query("select * from wallet where id = :id")
    Single<Wallet> getWalletById(Integer id);

    @Query("select count(*) from transact where wallet_id = :id")
    Single<Integer> countTransactInWallet(Integer id);

    @Query("select wallet_amount from wallet where id = :id")
    Single<Double> getWalletAmount(Integer id);

    @Query("update wallet set wallet_amount = wallet_amount - :amount where id = :id")
    void spendAmount(Integer id, Double amount);

    @Query("update wallet set wallet_amount = wallet_amount + :amount  where id = :id")
    void earnAmount(Integer id, Double amount);
}
