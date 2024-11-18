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

    @Query("update wallet set wallet_title = :new_title, syncState = :state where wallet_title = :old_title")
    Completable updateTitle(String old_title, String new_title, SyncState state);

    @Query("update wallet set wallet_amount = :wallet_amount, syncState = :state where wallet_title = :title")
    Completable updateAmount(String title, Double wallet_amount, SyncState state);

    @Query("update wallet set wallet_description = :wallet_description, syncState = :state where wallet_title = :title")
    Completable updateDescription(String title, String wallet_description, SyncState state);

    @Query("update wallet set last_sync_title = :title where wallet_title = :title")
    Completable updateLastSyncTitle(String title);

    //mark row as about to delete and not displaying
    @Query("update wallet set syncState = :state where wallet_title = :title")
    Completable deleteWallet(String title, SyncState state);

    //completely remove from db
    @Query("delete from wallet where wallet_title = :title")
    Completable removeWallet(String title);

    @Query("select syncState from wallet where wallet_title = :title")
    Single<SyncState> getState(String title);

    @Query("update wallet set syncState = :state where wallet_title = :title")
    Completable setState(String title, SyncState state);

    @Query("select * from wallet")
    Single<List<WalletWithTransacts>> getWalletWithTransactList();

    @Query("select * from wallet where syncState != :exclude_state")
    Single<List<Wallet>> getAllWalletWithState(SyncState exclude_state);

    @Query("select * from wallet")
    Single<List<Wallet>> getAllWallet();
}
