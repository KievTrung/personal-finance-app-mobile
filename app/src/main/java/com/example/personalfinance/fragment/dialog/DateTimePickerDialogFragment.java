package com.example.personalfinance.fragment.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.personalfinance.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Optional;

import kotlinx.coroutines.internal.LocalAtomics_commonKt;

public class DateTimePickerDialogFragment extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "kiev";

    public static class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public interface ConfirmDateDialogListener{
            void onConfirmClick(int year, int month, int date);
        }

        ConfirmDateDialogListener listener;

        public void setDateListener(ConfirmDateDialogListener listener) {
            this.listener = listener;
        }

        LocalDateTime now;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public DatePickerDialogFragment(LocalDateTime now){
            if (now == null)
                this.now = LocalDateTime.now();
            else
                this.now = now;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            int year = now.getYear();
            int month = now.getMonthValue();
            int date = now.getDayOfMonth();

            return new DatePickerDialog(getActivity(), this, year, month-1, date);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
            month++;
            listener.onConfirmClick(year, month, date);
        }
    }

    public static class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        public interface ConfirmTimeDialogListener{
            void onConfirmClick(int hourOfDay, int minute);
        }

        ConfirmTimeDialogListener listener;

        public void setTimeListener(ConfirmTimeDialogListener listener) {
            this.listener = listener;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            listener.onConfirmClick(hourOfDay, minute);
        }
    }

    private Integer year = null;
    private Integer month = null;
    private Integer date = null;
    private Integer hourOfDay = null;
    private Integer minute = null;
    private TextView textView;
    private String date__ = "";
    private String time = "";

    public interface ConfirmDateTimeDialogListener{
        void onConfirmClick(DialogInterface dialog, int year, int month, int date, int hourOfDay, int minute);
    }

    ConfirmDateTimeDialogListener listener;

    public void setDateTimeListener(ConfirmDateTimeDialogListener listener) {
        this.listener = listener;
    }

    LocalDateTime localDateTime;

    public DateTimePickerDialogFragment(){
        this.localDateTime = null;
    }

    public DateTimePickerDialogFragment(LocalDateTime localDateTime){
        this.localDateTime = localDateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_datetime_picker, null);
        v.findViewById(R.id.pick_date_btn).setOnClickListener(this);
        v.findViewById(R.id.pick_time_btn).setOnClickListener(this);
        textView = v.findViewById(R.id.date_textView);

        LocalDateTime now = LocalDateTime.now();
        year = now.getYear();
        month = now.getMonthValue();
        date = now.getDayOfMonth();
        hourOfDay = now.getHour();
        minute = now.getMinute();

        date__ = date + "/" + month + "/" + year;
        time = hourOfDay + ":" + minute;
        textView.setText(date__ + ", " + time);

        Optional<LocalDateTime> optionalS = Optional.ofNullable(localDateTime);
        if (optionalS.isPresent()){
            LocalDateTime date_ = optionalS.get();
            year = date_.getYear();
            month = date_.getMonthValue();
            date = date_.getDayOfMonth();
            hourOfDay = date_.getHour();
            minute = date_.getMinute();

            date__ = date + "/" + month + "/" + year;
            time = hourOfDay + ":" + minute;
            textView.setText(date__ + ", " + time);
        }

        builder.setView(v)
                .setPositiveButton("Confirm", (dialog, i) -> {
                    listener.onConfirmClick(dialog, year, month, date, hourOfDay, minute);
                })
                .setNegativeButton("Cancel", (dialog, i) -> dismiss());

        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pick_date_btn){
            DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment(localDateTime);
            datePickerDialogFragment.setDateListener(this::onDateConfirmClick);
            datePickerDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.pick_time_btn){
            TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
            timePickerDialogFragment.setTimeListener(this::onTimeConfirmClick);
            timePickerDialogFragment.show(getParentFragmentManager(), TAG);
        }
    }

    private void onDateConfirmClick(int year, int month, int date){
        this.year = year;
        this.month = month;
        this.date = date;
        date__ = date + "/" + month + "/" + year;
        textView.setText(date__ + ", " + time);
    }

    private void onTimeConfirmClick(int hourOfDay, int minute){
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        time = hourOfDay + ":" + minute;
        textView.setText(date__ + ", " + time);
    }
}
