package com.example.personalfinance.fragment.transaction;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;

public class WalletInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet_info, container, false);
        init();
        return v;
    }

    private MainActivity activity;

    private void init(){
        activity = (MainActivity)getActivity();
        activity.configToolbarToReturn(activity, getParentFragmentManager(), "Wallet information");
    }
}