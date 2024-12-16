package com.example.personalfinance.datalayer.local.entity.auxiliry;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeletedRow {
    public enum Table{ wallet, user, transact, item, category, budget, budgetCategory }
    @PrimaryKey(autoGenerate = true)
    private Integer delete_id;
    @NonNull
    private Integer id;
    @NonNull
    private Integer id2;
    @NonNull
    private Table table;

    public DeletedRow() {}

    public DeletedRow(@NonNull Integer id, @NonNull Table table) {
        this.id = id;
        this.id2 = -1;
        this.table = table;
    }

    public DeletedRow(@NonNull Integer id, @NonNull Integer id2, @NonNull Table table) {
        this.id = id;
        this.id2 = id2;
        this.table = table;
    }

    @NonNull
    public Integer getId2() {
        return id2;
    }

    public void setId2(@NonNull Integer id2) {
        this.id2 = id2;
    }

    public Integer getDelete_id() {
        return delete_id;
    }

    public void setDelete_id(Integer delete_id) {
        this.delete_id = delete_id;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public Table getTable() {
        return table;
    }

    public void setTable(@NonNull Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "DeletedRow{" +
                "id=" + id +
                ", table=" + table +
                '}';
    }
}
