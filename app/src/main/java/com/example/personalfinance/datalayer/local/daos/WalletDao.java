package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.datalayer.local.relationships.WalletWithTransacts;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface WalletDao {
    @Insert
    Completable insertWallet(Wallet wallet);

    @Query("update wallet set wallet_title = :new_title where wallet_title = :old_title")
    Completable udpateTitle(String old_title, String new_title);

    @Query("update wallet set wallet_amount = :wallet_amount where wallet_title = :title")
    Completable udpateAmount(String title, Double wallet_amount);

    @Query("update wallet set wallet_description = :wallet_description where wallet_title = :title")
    Completable updateSyncState(String title, String wallet_description);

    @Query("update wallet set syncState = :state where wallet_title = :title")
    Completable updateSyncState(String title, SyncState state);

    @Query("update wallet set removeState = 'pending' where wallet_title = :title")
    Completable deleteWallet(String title);

    @Query("select * from wallet")
    Single<List<WalletWithTransacts>> getWalletWithTransactList();

    @Query("select * from wallet where removeState = 'active'")
    Single<List<Wallet>> getAllWallets();
}
