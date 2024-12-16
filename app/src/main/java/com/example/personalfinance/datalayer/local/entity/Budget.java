package com.example.personalfinance.datalayer.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.SyncState;

import java.time.LocalDateTime;

@Entity(indices = {@Index(value = {"budget_title"}, unique = true)})
public class Budget {
    @PrimaryKey(autoGenerate = true)
    private Integer budget_id;
    @NonNull
    private String budget_title;
    @NonNull
    private Double budget_amount;
    @NonNull
    private LocalDateTime start_date;
    @NonNull
    private LocalDateTime end_date;
    @NonNull
    private CategoryType type;
    private SyncState syncState;

    public Budget(){
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    public Integer getBudget_id() {
        return budget_id;
    }

    public void setBudget_id(Integer budget_id) {
        this.budget_id = budget_id;
    }

    @NonNull
    public String getBudget_title() {
        return budget_title;
    }

    public void setBudget_title(@NonNull String budget_title) {
        this.budget_title = budget_title;
    }

    @NonNull
    public Double getBudget_amount() {
        return budget_amount;
    }

    public void setBudget_amount(@NonNull Double budget_amount) {
        this.budget_amount = budget_amount;
    }

    @NonNull
    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(@NonNull LocalDateTime start_date) {
        this.start_date = start_date;
    }

    @NonNull
    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(@NonNull LocalDateTime end_date) {
        this.end_date = end_date;
    }

    @NonNull
    public CategoryType getType() {
        return type;
    }

    public void setType(@NonNull CategoryType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + budget_id +
                ", budget_title='" + budget_title + '\'' +
                ", budget_amount=" + budget_amount +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", type=" + type +
                '}';
    }
}
