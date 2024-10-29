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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;

public class WalletFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        init(v);
        return v;
    }

    private MainActivity activity;

    private void init(View v){
        activity = (MainActivity)getActivity();
        activity.configToolbarToReturn(activity, getParentFragmentManager(), "My Wallet");
        ((Button)v.findViewById(R.id.create_wallet_btn)).setOnClickListener((view)->{
            ((MainActivity)getActivity()).replaceFragment(R.id.fragment_container, new WalletInfoFragment(), getContext(), true, null);
        });
    }
}