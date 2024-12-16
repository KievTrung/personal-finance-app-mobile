package com.example.personalfinance.datalayer.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity
public class Notify {
    public enum Type { budget }

    @PrimaryKey(autoGenerate = true)
    private Integer notify_id;
    @NonNull
    private Type type;
    @NonNull
    private String header;
    @NonNull
    private String content;
    @NonNull
    private LocalDateTime dateTime;
    @NonNull
    private Boolean isRead;

    public Notify(){}

    public Integer getNotify_id() {
        return notify_id;
    }

    @NonNull
    public Boolean getRead() {
        return isRead;
    }

    public void setRead(@NonNull Boolean read) {
        isRead = read;
    }

    public void setNotify_id(Integer notify_id) {
        this.notify_id = notify_id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

   @Override
    public String toString() {
        return "Notify{" +
                "notify_id=" + notify_id +
                ", type=" + type +
                ", header='" + header + '\'' +
                ", content='" + content + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
