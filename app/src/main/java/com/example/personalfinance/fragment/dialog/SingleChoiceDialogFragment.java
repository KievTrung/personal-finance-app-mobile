package com.example.personalfinance.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SingleChoiceDialogFragment extends DialogFragment {
    private static final String TAG = "SingleChoiceDialogFragm";
    private String[] choices;
    private String choice;
    private int checkedItem;
    private String title = null;
    private String positiveBtnTitle = "Confirm", negativeBtnTitle = "Cancel";
    private DialogInterface.OnClickListener positiveListener = (dialog, i) -> dismiss();
    private DialogInterface.OnClickListener negativeListener = (dialog, i) -> dismiss();
    private DialogInterface.OnClickListener choiceBtnListener = (dialog, i) -> pickChoices(i);

    public SingleChoiceDialogFragment(String title, String[] choices, int checkedItem) throws Exception {
        if (choices.length < 2 || choices.length < checkedItem)
            throw new Exception("choices length must be greater than 1");

        this.title = title;
        this.checkedItem = checkedItem;
        this.positiveListener = positiveListener;
        this.choices = choices;
        this.choice = choices[checkedItem];
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setPositiveButton(positiveBtnTitle, positiveListener)
                .setNegativeButton(negativeBtnTitle, negativeListener)
                .setSingleChoiceItems(choices, checkedItem, choiceBtnListener);

        return builder.create();
    }

    private void pickChoices(int i){ choice = choices[i];}

    public String getChoice() {
        return choice;
    }

    public void setPositiveListener(DialogInterface.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }
}
