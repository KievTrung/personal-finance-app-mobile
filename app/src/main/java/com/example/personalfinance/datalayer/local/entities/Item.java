package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.SyncState;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Transact.class,
                        parentColumns = "id",
                        childColumns = "bill_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class Item {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @NonNull
    private Integer bill_id;
    @NonNull
    private String item_name;
    @NonNull
    private Integer quantity;
    @NonNull
    private Double item_price;
    @NonNull
    private SyncState syncState;

    public Item() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NonNull
    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(@NonNull SyncState syncState) {
        this.syncState = syncState;
    }

    @NonNull
    public Integer getBill_id() {
        return bill_id;
    }

    public void setBill_id(@NonNull Integer bill_id) {
        this.bill_id = bill_id;
    }

    @NonNull
    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(@NonNull String item_name) {
        this.item_name = item_name;
    }

    @NonNull
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NonNull Integer quantity) {
        this.quantity = quantity;
    }

    @NonNull
    public Double getItem_price() {
        return item_price;
    }

    public void setItem_price(@NonNull Double item_price) {
        this.item_price = item_price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", bill_id=" + bill_id +
                ", item_name='" + item_name + '\'' +
                ", quantity=" + quantity +
                ", item_price=" + item_price +
                '}';
    }
}
