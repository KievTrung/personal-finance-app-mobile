package com.example.personalfinance.fragment.transaction;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalfinance.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartPieFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        init(v);
        return v;
    }

    private PieChart pieChart;

    private void init(View v){
        pieChart = (PieChart) v.findViewById(R.id.pieChart);

        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.animateY(1000, Easing.EaseInOutCubic);

        List<PieEntry> pEntry = new ArrayList<>();
        pEntry.add(new PieEntry(34, "bld"));
        pEntry.add(new PieEntry(23, "usa"));
        pEntry.add(new PieEntry(14, "us"));
        pEntry.add(new PieEntry(35, "India"));
        pEntry.add(new PieEntry(40, "ru"));
        pEntry.add(new PieEntry(23, "jpn"));
        pEntry.add(new PieEntry(38, "bld"));

        PieDataSet pieDataSet = new PieDataSet(pEntry, "Contries");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);

        pieChart.setData(pieData);
    }
}