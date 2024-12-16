package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entity.UseWallet;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface UseWalletDao {
    @Query("delete from usewallet")
    void deleteUseWallet();

    @Insert
    void insertUseWallet(UseWallet useWallet);

    @Query("select id from usewallet limit 1")
    long getUseWallet();

    @Query("select id from usewallet limit 1")
    Single<Integer> getUseWalletObservable();
}
