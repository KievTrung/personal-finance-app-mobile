package com.example.personalfinance.fragment.dialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.personalfinance.R;

import java.util.Calendar;

public class DateTimePickerDialogFragment extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "DateTimePickerDialogFra";

    public static class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int date = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, date);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            // TODO: 10/27/2024 add functionality
        }
    }

    public static class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.YEAR);
            int minute = c.get(Calendar.MONTH);

            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            // TODO: 10/27/2024 add functionality
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_datetime_picker, null);
        ((Button)v.findViewById(R.id.pick_date_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.pick_time_btn)).setOnClickListener(this);

        builder.setView(v)
                .setPositiveButton("Confirm", (dialog, i) -> dismiss()/*TODO: add functionality*/)
                .setNegativeButton("Cancel", (dialog, i) -> dismiss());

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pick_date_btn){
            new DatePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.pick_time_btn){
            new TimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
    }
}
