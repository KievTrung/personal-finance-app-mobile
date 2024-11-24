package com.example.personalfinance.datalayer.local.daos.auxiliry;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entities.auxiliry.DeletedRow;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface DeletedRowDao {
//    @Query("select id from DeletedRow")
//    Single<List<Integer>> getAll();

    @Insert
    void insert(DeletedRow row);
}
