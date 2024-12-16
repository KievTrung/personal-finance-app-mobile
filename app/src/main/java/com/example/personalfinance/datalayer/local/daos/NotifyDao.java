package com.example.personalfinance.datalayer.local.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personalfinance.datalayer.local.entity.Notify;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotifyDao {
    @Insert
    long insertNotify(Notify notify);

    @Query("update notify set isRead = 1 where notify_id = :id")
    Completable hasRead(Integer id);

    @Query("delete from notify where notify_id = :id")
    Completable deleteNotify(Integer id);

    @Query("select * from notify order by dateTime desc")
    Single<List<Notify>> getAll();
}
