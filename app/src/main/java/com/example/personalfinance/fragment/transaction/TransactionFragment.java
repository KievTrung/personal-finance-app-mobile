package com.example.personalfinance.fragment.transaction;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.dialogFragment.DateTimePickerDialogFragment;
import com.example.personalfinance.fragment.dialogFragment.SingleChoiceDialogFragment;
import com.example.personalfinance.model.BarChartData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TransactionFragment";
    private MainActivity activity;
    private View v;

    private enum Chart{ BAR, PIE; }
    private enum ViewType { CHART, LIST, BOTH; }
    private Chart chart;
    private ViewType viewType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Transaction onCreateView");
        v = inflater.inflate(R.layout.fragment_transaction, container, false);
        init(v);
        return v;
    }

    private void init(View v){
        activity = (MainActivity)getActivity();

        //set up view
        viewType = ViewType.BOTH;

        //set up bar chart
        activity.replaceFragment(R.id.chart_fragment_view, new ChartBarFragment(), getContext(), false, null, null);
        chart = Chart.BAR;

        //set up tool bar
        activity.configToolBarTopRightBtn(activity, View.VISIBLE, R.drawable.wallet, (view)-> activity.replaceFragment(R.id.fragment_container, new WalletFragment(), getContext(), true, null, null));
        activity.setToolBarMenuBtnVisibility(activity, View.VISIBLE);
        activity.setToolBarHeaderText(activity, null);

        //set up fragment button
        ((Button)v.findViewById(R.id.wallet_detail_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.create_transaction_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.transaction_begin_date_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.transaction_end_date_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.chart_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.view_btn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.wallet_detail_btn){
            activity.replaceFragment(R.id.fragment_container, new WalletInfoFragment(), getContext(), true, null, null);
        }
        else if (id == R.id.create_transaction_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForNewTransactionDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.transaction_begin_date_btn){
            new DateTimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.transaction_end_date_btn){
            new DateTimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.chart_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForChartDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.view_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForViewDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
    }

    private SingleChoiceDialogFragment getSingleChoiceForNewTransactionDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Transaction", "Bill"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("Transaction type", choices, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            Bundle result = new Bundle();
            if (singleChoiceDialogFragment.getChoice().equals(choices[0])) result.putString("bundleKey", "transaction");
            else result.putString("bundleKey", "bill");
            getParentFragmentManager().setFragmentResult("requestKey", result);

            activity.replaceFragment(R.id.fragment_container, new ActualTransactionFragment(), getContext(), true, null, null);
        });
        return singleChoiceDialogFragment;
    }

    private SingleChoiceDialogFragment getSingleChoiceForViewDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Both", "Chart", "List"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("View type", choices, (viewType == ViewType.BOTH) ? 0 : ((viewType == ViewType.CHART) ? 1 : 2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            int chartView = 0, listView = 0;
            if (singleChoiceDialogFragment.getChoice().equals(choices[0]) && viewType != ViewType.BOTH){
                chartView = View.VISIBLE;
                listView = View.VISIBLE;
                viewType = ViewType.BOTH;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[1]) && viewType != ViewType.CHART){
                chartView = View.VISIBLE;
                listView = View.GONE;
                viewType = ViewType.CHART;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[2]) && viewType != ViewType.LIST){
                chartView = View.GONE;
                listView = View.VISIBLE;
                viewType = ViewType.LIST;
            }

            if (listView == View.GONE) squeezeInView(v.findViewById(R.id.transaction_list));
            else if (listView == View.VISIBLE) squeezeOutView(v.findViewById(R.id.transaction_list));

            if (chartView == View.GONE) squeezeInView(v.findViewById(R.id.chart_fragment_view));
            else if (chartView == View.VISIBLE) squeezeOutView(v.findViewById(R.id.chart_fragment_view));
        });
        return singleChoiceDialogFragment;
    }

    private SingleChoiceDialogFragment getSingleChoiceForChartDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Bar", "Pie"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("Chart type", choices, (chart == Chart.BAR) ? 0 : 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            if (singleChoiceDialogFragment.getChoice().equals(choices[0]) && chart != Chart.BAR){
                activity.replaceFragment(R.id.chart_fragment_view, new ChartBarFragment(), getContext(), false, null, null);
                chart = Chart.BAR;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[1]) && chart != Chart.PIE){
                activity.replaceFragment(R.id.chart_fragment_view, new ChartPieFragment(), getContext(), false, null, null);
                chart = Chart.PIE;
            }
        });
        return singleChoiceDialogFragment;
    }

    public void squeezeInView(final View view) {
        view.animate()
                .scaleY(0f)
                .setDuration(500)
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    public void squeezeOutView(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setScaleY(0f);
        view.setPivotY(view.getHeight());
        view.animate()
                .scaleY(1f)
                .setDuration(500)
                .start();
    }


    @Override
    public void onStop() {
        activity.getTopRightBtnReference(activity).setVisibility(View.INVISIBLE);
        super.onStop();
    }

}