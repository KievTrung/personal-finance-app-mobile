package com.example.personalfinance.fragment.transaction;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;

public class BillItemFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bill_item, container, false);
        init(v);
        return v;
    }

    private MainActivity activity;

    private void init(View v){
        activity = (MainActivity)getActivity();

        ((Button)v.findViewById(R.id.category_items_2_btn)).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        activity.setToolBarHeaderText(activity, "New item");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.category_items_2_btn){
            activity.replaceFragment(R.id.fragment_container, new CategoryFragment(), getContext(),true, null, null);
        }
    }
}