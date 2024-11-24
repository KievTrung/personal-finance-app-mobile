package com.example.personalfinance.datalayer.local.entities.auxiliry;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"id", "table"})
public class DeletedRow {
    public enum Table{
        wallet, user, transact, item, category
    }
    @NonNull
    private Integer id;
    @NonNull
    private Table table;

    public DeletedRow() {}

    public DeletedRow(@NonNull Integer id, @NonNull Table table) {
        this.id = id;
        this.table = table;
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
