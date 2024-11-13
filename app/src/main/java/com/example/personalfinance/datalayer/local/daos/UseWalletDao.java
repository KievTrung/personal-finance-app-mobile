package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.personalfinance.datalayer.local.entities.UseWallet;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UseWalletDao {
    @Query("delete from usewallet")
    Completable deleteUseWallet();

    @Insert
    Completable insertUseWallet(UseWallet useWallet);

    default Completable setUseWallet(UseWallet useWallet){
        return deleteUseWallet().andThen(insertUseWallet(useWallet));
    }

    @Query("select wallet_title from usewallet limit 1")
    Single<String> getUseWallet();
}
