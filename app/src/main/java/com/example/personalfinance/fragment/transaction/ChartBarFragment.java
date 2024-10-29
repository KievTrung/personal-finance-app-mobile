package com.example.personalfinance.fragment.transaction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalfinance.R;
import com.example.personalfinance.model.BarChartData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;

public class ChartBarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        init(v);
        return v;
    }

    private BarChart barChart;
    private BarChartData barChartData = new BarChartData();

    private void init(View v){
        //set up chart
        barChart = (BarChart)v.findViewById(R.id.barChart);
        barChartData.addData(1f, 2f);
        barChartData.addData(3f, 4f);
        barChartData.addData(5f, 6f);
        barChartData.addData(7f, 8f);
        barChartData.addData(9f, -10f);
        barChartData.addData(11f, 12f);
        barChartData.addData(13f, 14f);
        barChartData.addData(15f, 16f);
        barChartData.addData(17f, 18f);
        barChartData.addData(19f, 20f);
        BarDataSet barDataSet = new BarDataSet(barChartData.getEntries(), "hello");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.setPinchZoom(true);
        barChart.setHighlightPerDragEnabled(false);
        barChart.invalidate();
        barChart.animateY(1000, Easing.EaseInOutCubic);
    }
}