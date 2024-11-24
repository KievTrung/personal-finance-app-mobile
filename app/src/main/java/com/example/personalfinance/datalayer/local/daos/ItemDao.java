package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.entities.Item;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface ItemDao {
    @Insert
    void insertItem(Item item);

    @Update
    void updateItem(Item item);

    @Query("select * from item where bill_id = :id")
    Single<List<Item>> getAllBelongToBill(Integer id);

    @Query("delete from item where bill_id = :id")
    void deleteItem(Integer id);
}
