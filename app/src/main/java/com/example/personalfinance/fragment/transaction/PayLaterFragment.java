package com.example.personalfinance.fragment.transaction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.dialog.DateTimePickerDialogFragment;

public class PayLaterFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "PayLaterFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pay_later, container, false);
        init(v);
        return v; 
    }

    private MainActivity activity;

    private void init(View v){
        activity = (MainActivity)getActivity();
        activity.configToolbarToReturn(view -> getParentFragmentManager().popBackStack());
        activity.setToolBarHeaderText("Pay later");

        ((Button)v.findViewById(R.id.pick_paylater_begin_date_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.pick_paylater_end_date_btn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pick_paylater_begin_date_btn){
            new DateTimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.pick_paylater_end_date_btn){
            new DateTimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
    }
}