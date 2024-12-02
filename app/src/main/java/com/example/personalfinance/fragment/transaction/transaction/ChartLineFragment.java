package com.example.personalfinance.fragment.transaction.transaction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;
import com.example.personalfinance.fragment.transaction.transaction.model.TransactModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChartLineFragment extends Fragment {
    private static final String TAG = "kiev";
    private TransactionViewModel transactionViewModel;

    public class LocalDateTimeFormatter extends ValueFormatter {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override public String getAxisLabel(float value, AxisBase axis) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli((long) value), ZoneOffset.UTC);
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd"));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init viewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
    }

    @RequiresApi(api = 35)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        init(v);
        return v;
    }

    private LineChart chart;
    private List<Entry> entries = new ArrayList<>();

    TextView textView;
    TransactModel tran = null;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = 35)
    private void init(View v){
        //init component
        textView = v.findViewById(R.id.description);
        chart = v.findViewById(R.id.lineChart);

        //get bundle
        assert getArguments() != null;
        Bundle bundle = getArguments();

        //get list transact
        List<TransactModel> trans = bundle.getParcelableArrayList("payload");
        assert trans != null;
        if (trans.isEmpty()) return;

        //get currency
        Currency currency = Currency.valueOf(bundle.getString("currency"));

        //create entry of graph
        trans.sort(Comparator.comparing(TransactModel::getDate_time));

        for (TransactModel tran : trans){
            // Convert LocalDateTime to timestamp in milliseconds
            long timestamp = tran.getDate_time().toInstant(ZoneOffset.UTC).toEpochMilli();
            float yVal = (UserRepository.toCurrency((tran.getCategoryModel().getCategoryType() == CategoryType.earning) ? tran.getTran_amount() : - tran.getTran_amount(), currency)).floatValue();
            entries.add(new Entry(timestamp, yVal));
        }

        //add colors
        List<Integer> colors = new ArrayList<>();
        for (Entry entry : entries){
            if (entry.getY() < 0)
                colors.add(ColorTemplate.rgb("D25C5C"));
            else
                colors.add(ColorTemplate.rgb("41CD19"));
        }
        colors.remove(0);
        if (entries.get(entries.size() - 1).getY() < 0)
            colors.add(ColorTemplate.rgb("D25C5C"));
        else
            colors.add(ColorTemplate.rgb("41CD19"));

        int arr[] = colors.stream().mapToInt(Integer::intValue).toArray();

        // Create a LineDataSet for each entry
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(arr);
        dataSet.setCircleColors(arr);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);
        LineData data = new LineData(dataSet);

        //set up graph
        chart.setData(data);
        chart.setPinchZoom(true);
        chart.setHighlightPerDragEnabled(false);
        chart.invalidate();
        chart.animateY(1000, Easing.EaseInOutCubic);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);

        //set chart listener
        List<TransactModel> finalTrans = trans;
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //get index of this entry in list
                tran = finalTrans.get(entries.indexOf(e));
                String text = "Title: " + tran.getTran_title() + "\n" +
                            "Date: " + tran.getDate_time().format(DateTimeFormatter.ofPattern("d/MM/yyyy - HH:mm")) + "\n" +
                            "Amount: " + ((e.getY() < 0) ? " " : "+") + UserRepository.formatNumber(UserRepository.toCurrency(tran.getTran_amount(), currency), true, currency);
                textView.setVisibility(View.VISIBLE);
                textView.setText(text);
            }

            @Override
            public void onNothingSelected() {
                textView.setVisibility(View.GONE);
                tran = null;
            }
        });
        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                chart.fitScreen();
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
        textView.setOnLongClickListener(v1 -> {
            if (tran != null){
                transactionViewModel.compositeDisposable.add(
                        transactionViewModel
                                .get(tran.getTran_id())
                                .subscribe(transactModel -> {
                                    //update the transaction
                                    TransactionViewModel.tempTransact = transactModel;
                                    TransactionViewModel.action = TransactionViewModel.Action.update;

                                    if (TransactionViewModel.tempTransact.getType() == Transact.Type.bill){
                                        transactionViewModel.compositeDisposable.add(
                                                transactionViewModel
                                                        .getAllItem(TransactionViewModel.tempTransact.getTran_id())
                                                        .subscribe(itemModels -> {
                                                            TransactionViewModel.tempTransact.setItems(itemModels);
                                                            ((MainActivity)getActivity()).replaceFragment(new NewTransactionFragment(), true, null);
                                                        })
                                        );
                                    }
                                    else
                                        ((MainActivity)getActivity()).replaceFragment(new NewTransactionFragment(), true, null);
                                })
                );
            }
            return true;
        });

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new LocalDateTimeFormatter());
        xAxis.setXOffset(50f);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(60000f);

        YAxis yAxis_left = chart.getAxisLeft();
        yAxis_left.setValueFormatter(new LargeValueFormatter());
        yAxis_left.setDrawGridLines(false);
        yAxis_left.setDrawZeroLine(true);
        yAxis_left.setZeroLineWidth(1f);
        yAxis_left.setTextSize(10f);

        YAxis yAxis_right = chart.getAxisRight();
        yAxis_right.setEnabled(false);
        yAxis_right.setDrawGridLines(false);
    }
}