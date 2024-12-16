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

    public interface NoticeCancelDialogListener{
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;
    NoticeCancelDialogListener cancelListener = null;

    public void setNoticeDialogListener(NoticeDialogListener noticeDialogListener){
        this.listener = noticeDialogListener;
    }

    public void setCancelListener(NoticeCancelDialogListener cancelListener) {
        this.cancelListener = cancelListener;
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
                .setNegativeButton("Cancel", (dialog, id) -> {
                    if (cancelListener != null)
                        cancelListener.onDialogNegativeClick(this);
                    else
                        dismiss();
                });

        return builder.create();
    }
}
