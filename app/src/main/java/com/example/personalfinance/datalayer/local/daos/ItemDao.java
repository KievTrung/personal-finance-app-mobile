package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalfinance.datalayer.local.entities.Item;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface ItemDao {
    class IdAndSyncstate{
        public Integer id;
        public SyncState syncState;
        public IdAndSyncstate(Integer id, SyncState syncState){
            this.id = id;
            this.syncState = syncState;
        }
    }
    @Insert
    void insertItem(Item item);

    @Update
    void updateItem(Item item);

    @Query("select id, syncState from item where bill_id = :billId")
    List<IdAndSyncstate> getIdAndSyncstate(Integer billId);

    @Query("delete from item where id = :id")
    void deleteItem(Integer id);

    @Query("select syncState from item where id = :id")
    SyncState getState(Integer id);

    @Query("select * from item where bill_id = :billId")
    Single<List<Item>> getAllItemBelongTo(Integer billId);
}
