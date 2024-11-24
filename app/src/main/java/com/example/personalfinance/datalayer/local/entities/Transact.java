package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.Period;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.time.LocalDateTime;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Wallet.class,
                        parentColumns = "id",
                        childColumns = "wallet_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class Transact{
    public enum Type{ transaction, bill}

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @NonNull
    private Integer wallet_id;
    @NonNull
    private Integer category_id;
    private Type type;
    @NonNull
    private String title;
    @NonNull
    private LocalDateTime date_time;
    @NonNull
    private Double amount;
    private Period auto_tran;
    private String description;
    private SyncState syncState;

    public Transact() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setDate_time(@NonNull LocalDateTime date_time) {
        this.date_time = date_time;
    }

    @NonNull
    public Double getAmount() {
        return amount;
    }

    public void setAmount(@NonNull Double amount) {
        this.amount = amount;
    }

    public Period getAuto_tran() {
        return auto_tran;
    }

    public void setAuto_tran(Period auto_tran) {
        this.auto_tran = auto_tran;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    @NonNull
    public Integer getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(@NonNull Integer wallet_id) {
        this.wallet_id = wallet_id;
    }

    @NonNull
    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(@NonNull Integer category_id) {
        this.category_id = category_id;
    }

    @Override
    public String toString() {
        return "Transact{" +
                "wallet_id=" + wallet_id +
                ", category_id=" + category_id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", date_time=" + date_time +
                ", amount=" + amount +
                ", auto_tran=" + auto_tran +
                ", description='" + description + '\'' +
                ", syncState=" + syncState +
                '}';
    }
}
