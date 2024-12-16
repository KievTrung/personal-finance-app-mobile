package com.example.personalfinance.datalayer.local.daos.auxiliry;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.personalfinance.datalayer.local.entity.auxiliry.DeletedRow;

@Dao
public interface DeletedRowDao {
//    @Query("select id from DeletedRow")
//    Single<List<Integer>> getAll();

    @Insert
    void insert(DeletedRow row);
}
