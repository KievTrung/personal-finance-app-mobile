package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.Period;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Wallet.class,
                        parentColumns = "wallet_title",
                        childColumns = "wallet_title",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class Transact {
    @PrimaryKey(autoGenerate = true)
    private Integer tran_id;
    @NonNull
    private String wallet_title;
    @NonNull
    private String tran_title;
    @NonNull
    private String category_id;
    @NonNull
    private LocalDateTime date_time;
    @NonNull
    private Long tran_amount;
    private Period auto_tran;
    private String tran_description;

    public Transact(){}

    public String getWallet_title() {
        return wallet_title;
    }

    public void setWallet_title(String wallet_title) {
        this.wallet_title = wallet_title;
    }

    public String getTran_description() {
        return tran_description;
    }

    public void setTran_description(String tran_description) {
        this.tran_description = tran_description;
    }

    public Period getAuto_tran() {
        return auto_tran;
    }

    public void setAuto_tran(Period auto_tran) {
        this.auto_tran = auto_tran;
    }

    public Long getTran_amount() {
        return tran_amount;
    }

    public void setTran_amount(Long tran_amount) {
        this.tran_amount = tran_amount;
    }

    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setDate_time(LocalDateTime date_time) {
        this.date_time = date_time;
    }

    public String getTran_title() {
        return tran_title;
    }

    public void setTran_title(String tran_title) {
        this.tran_title = tran_title;
    }

    public Integer getTran_id() {
        return tran_id;
    }

    public void setTran_id(Integer tran_id) {
        this.tran_id = tran_id;
    }

    @NonNull
    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(@NonNull String category_id) {
        this.category_id = category_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transact transact = (Transact) o;
        return Objects.equals(tran_id, transact.tran_id) &&
                Objects.equals(wallet_title, transact.wallet_title)
                && Objects.equals(tran_title, transact.tran_title)
                && Objects.equals(date_time, transact.date_time)
                && Objects.equals(category_id, transact.category_id)
                && Objects.equals(tran_amount, transact.tran_amount)
                && auto_tran == transact.auto_tran
                && Objects.equals(tran_description, transact.tran_description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tran_id,
                wallet_title,
                tran_title,
                date_time,
                tran_amount,
                auto_tran,
                tran_description);
    }

    @Override
    public String toString() {
        return "Transact{" +
                "tran_id=" + tran_id +
                ", wallet_title='" + wallet_title + '\'' +
                ", tran_title='" + tran_title + '\'' +
                ", date_time=" + date_time +
                ", tran_amount=" + tran_amount +
                ", auto_tran=" + auto_tran +
                ", tran_description='" + tran_description + '\'' +
                '}';
    }
}
