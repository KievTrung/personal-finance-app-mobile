package com.example.personalfinance.fragment.transaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.personalfinance.R;

public class CategoryNewDialogFragment extends DialogFragment implements View.OnClickListener{
    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_new_category, null);
        ((Button)v.findViewById(R.id.add_symbol_btn)).setOnClickListener(this);

        builder.setView(v)
                .setPositiveButton("Save", (dialog, id) -> dismiss() /*TODO: add functionality*/)
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        // TODO: 10/27/2024 add functionality
    }
}
