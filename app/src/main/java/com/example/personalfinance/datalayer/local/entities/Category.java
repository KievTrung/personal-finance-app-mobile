package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.SyncState;

@Entity(indices = {@Index(value = {"categoryName", "categoryType"}, unique = true)})
public class Category {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @NonNull
    private String categoryName;
    private String last_sync_name;
    @NonNull
    private CategoryType categoryType;
    @NonNull
    private SyncState syncState;

    public Category(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLast_sync_name() {
        return last_sync_name;
    }

    public void setLast_sync_name(String last_sync_name) {
        this.last_sync_name = last_sync_name;
    }

    @NonNull
    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(@NonNull SyncState syncState) {
        this.syncState = syncState;
    }

    @NonNull
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    @NonNull
    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(@NonNull CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", last_sync_name='" + last_sync_name + '\'' +
                ", categoryType=" + categoryType +
                ", syncState=" + syncState +
                '}';
    }
}
