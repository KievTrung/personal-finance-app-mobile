package com.example.personalfinance.fragment.transaction;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.fragment.transaction.model.TransactModel;
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
        //set up component
        pieChart = v.findViewById(R.id.pieChart);

        //get bundle
        assert getArguments() != null;
        Bundle bundle = getArguments();

        //get list transact
        List<TransactModel> trans = bundle.getParcelableArrayList("payload");
        assert trans != null;
        if (trans.isEmpty()) return;

        float spend = 0, earn = 0;

        //create entry of graph
        for (TransactModel tran : trans){
            if (tran.getCategoryModel().getCategoryType() == CategoryType.spending)
                spend += tran.getTran_amount().floatValue();
            else
                earn += tran.getTran_amount().floatValue();
        }

        List<PieEntry> pEntry = new ArrayList<>();
        pEntry.add(new PieEntry(spend, "SPEND"));
        pEntry.add(new PieEntry(earn, "EARN"));

        PieDataSet pieDataSet = new PieDataSet(pEntry, null);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setColors(ColorTemplate.rgb("D25C5C"), ColorTemplate.rgb("41CD19"));

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.YELLOW);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
    }
}