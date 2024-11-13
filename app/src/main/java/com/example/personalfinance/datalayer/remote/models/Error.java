package com.example.personalfinance.datalayer.remote.models;

import android.os.Build;

import androidx.annotation.RequiresApi;
import java.time.LocalDateTime;

public class Error {
    private String status;
    private String msg;
    private LocalDateTime timeStamp;

    public Error() {}

    public String getStatus() {
        return status;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Error(String status, String msg, String timeStamp) {
        this.status = status;
        this.msg = msg;
        this.timeStamp = LocalDateTime.parse(timeStamp);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = LocalDateTime.parse(timeStamp) ;
    }

    @Override
    public String toString() {
        return "Error{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
