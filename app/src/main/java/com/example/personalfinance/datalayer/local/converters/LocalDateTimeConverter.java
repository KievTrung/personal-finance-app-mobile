package com.example.personalfinance.datalayer.local.converters;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static LocalDateTime stringToDate(String dateTime){
        return dateTime == null ? null : LocalDateTime.parse(dateTime);
    }

    @TypeConverter
    public static String dateToString(LocalDateTime dateTime){
        return dateTime == null ? null : dateTime.toString();
    }
}
