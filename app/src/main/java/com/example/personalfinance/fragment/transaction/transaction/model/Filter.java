package com.example.personalfinance.fragment.transaction.transaction.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.personalfinance.fragment.category.CategoryModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Filter implements Serializable {
    public enum Type { all, transaction, bill}
    public enum Sort { high_to_low, low_to_high, oldest, latest}

    public String title;
    public Type type;
    public Sort sort;
    public LocalDateTime from, to;
    public List<CategoryModel> categories;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Filter(){
        title = "";
        type = Type.all;
        sort = Sort.latest;
        categories = new ArrayList<>();

        //set begin date of this month
        from = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);

        LocalDateTime now = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonthValue());
        int lastDayOfThisMonth = yearMonth.lengthOfMonth();

        //set end date of this month
        to = LocalDateTime.now().withDayOfMonth(lastDayOfThisMonth).withHour(23).withMinute(59);
    }

    public void addCategory(CategoryModel category){
        if (categories.contains(category))
            throw new RuntimeException("This category already existed");
        categories.add(category);
    }

    @Override
    public String toString() {
        return "Filter{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", sort=" + sort +
                ", from=" + from +
                ", to=" + to +
                ", categories=" + categories +
                '}';
    }
}
