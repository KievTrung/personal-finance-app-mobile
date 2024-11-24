package com.example.personalfinance.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialogFragment extends DialogFragment {

    public interface NoticeDialogListener{
        void onDialogPositiveClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    public void setNoticeDialogListener(NoticeDialogListener noticeDialogListener){
        this.listener = noticeDialogListener;
    }

    public static ConfirmDialogFragment newInstance(String header){
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("header", header);
        confirmDialogFragment.setArguments(bundle);
        return confirmDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getArguments().getString("header"))
                .setPositiveButton("Confirm", (dialog, id) -> listener.onDialogPositiveClick(this))
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }
}
