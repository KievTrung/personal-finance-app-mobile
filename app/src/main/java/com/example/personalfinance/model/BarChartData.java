package com.example.personalfinance.model;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class BarChartData {
    private List<BarEntry> entries;

    public BarChartData() {
        entries = new ArrayList<>();
    }

    public List<BarEntry> getEntries() {
        return entries;
    }

    public void addData(Float x, Float y){
        entries.add(new BarEntry(x, y));
    }
}
