package com.example.personalfinance.fragment.transaction.transaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.Period;
import com.example.personalfinance.fragment.category.CategoryFragment;
import com.example.personalfinance.fragment.category.CategoryModel;
import com.example.personalfinance.fragment.category.adapter.CategoryRecyclerViewAdapter;
import com.example.personalfinance.fragment.dialog.DateTimePickerDialogFragment;
import com.example.personalfinance.fragment.transaction.transaction.model.Filter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FilterDialogFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "kiev";
    EditText title;
    Spinner type, sort;
    Button from, to, category;
    RecyclerView recyclerView;
    CategoryRecyclerViewAdapter adapter;
    Filter filter;

    public FilterDialogFragment(Filter filter) {
        this.filter = filter;
    }

    public interface ConfirmFilterDialogListener{
        void onConfirmClick(DialogInterface dialog, Filter filter);
    }

    ConfirmFilterDialogListener listener;

    public void setListener(ConfirmFilterDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    //reset filter
                    filter = new Filter();
                    title.setText("");
                    setTypeSpinner(filter.type);
                    setSortSpinner(filter.sort);
                    from.setText(NewTransactionFragment.printDate(filter.from));
                    to.setText(NewTransactionFragment.printDate(filter.to));
                    adapter.update(filter.categories);
                    Toast.makeText(requireContext(), "Reset successfully", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //set layout
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = requireActivity().getLayoutInflater().inflate(R.layout.dialog_transact_filter, null);

        title = v.findViewById(R.id.filter_title_et);
        title.setText(filter.title);

        type = v.findViewById(R.id.filter_type_spinner);
        type.setOnItemSelectedListener(this);
        setTypeSpinner(filter.type);

        sort = v.findViewById(R.id.filter_sort_spinner);
        sort.setOnItemSelectedListener(this);
        setSortSpinner(filter.sort);

        from = v.findViewById(R.id.filter_begin_date_btn);
        from.setOnClickListener(this);
        from.setText(NewTransactionFragment.printDate(filter.from));

        to = v.findViewById(R.id.filter_end_date_btn);
        to.setOnClickListener(this);
        to.setText(NewTransactionFragment.printDate(filter.to));

        category = v.findViewById(R.id.filter_category_btn);
        category.setOnClickListener(this);

        //init adapter
        adapter = new CategoryRecyclerViewAdapter();
        adapter.setItemOnClickListener(position -> {/*do nothing*/});
        adapter.setItemOnLongClickListener(position -> {
           //delete item
            filter.categories.remove(position);
            adapter.update(filter.categories);
            Toast.makeText(requireContext(), "Deleting successfully", Toast.LENGTH_SHORT).show();
        });
        adapter.update(filter.categories);
        //init recycler view
        recyclerView = v.findViewById(R.id.recycler_view_filter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        builder.setView(v)
                .setTitle("Custom filter")
                .setPositiveButton("Filter", (dialog, i) -> {
                    filter.title = title.getText().toString().trim();
                    listener.onConfirmClick(dialog, filter);
                })
                .setNeutralButton("Reset", null)
                .setNegativeButton("Cancel", (dialog, i) -> dismiss());
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.filter_begin_date_btn){
            Log.d(TAG, "onClick: " + filter.from);
            DateTimePickerDialogFragment dialog = new DateTimePickerDialogFragment(filter.from);
            dialog.setDateTimeListener((dialog1, year, month, date1, hourOfDay, minute) -> {
                LocalDateTime temp = LocalDateTime.of(year, month, date1, hourOfDay, minute);
                if (temp.isAfter(filter.to))
                    Toast.makeText(requireContext(), "Begin date must before end date, please try again !", Toast.LENGTH_SHORT).show();
                else{
                    filter.from = temp;
                    from.setText(NewTransactionFragment.printDate(filter.from));
                }
            });
            dialog.show(getParentFragmentManager(), null);
        }
        else if (id == R.id.filter_end_date_btn){
            DateTimePickerDialogFragment dialog = new DateTimePickerDialogFragment(filter.to);
            dialog.setDateTimeListener((dialog1, year, month, date1, hourOfDay, minute) -> {
                LocalDateTime temp = LocalDateTime.of(year, month, date1, hourOfDay, minute);
                if (temp.isBefore(filter.to))
                    Toast.makeText(requireContext(), "End date must after begin date, please try again !", Toast.LENGTH_SHORT).show();
                else{
                    filter.to = temp;
                    to.setText(NewTransactionFragment.printDate(filter.to));
                }
            });
            dialog.show(getParentFragmentManager(), null);
        }
        else if (id == R.id.filter_category_btn){
            ((MainActivity)getActivity()).replaceFragment(new CategoryFragment(),true, null);
            TransactionViewModel.requestAddCategoryToFilter = true;
            this.dismiss();
        }
    }

    private boolean isTypeInit = false;
    private boolean isSortInit = false;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id_) {
        int id = parent.getId();
        if (id == R.id.filter_type_spinner){
            if (isTypeInit)
                switch(position){
                    case 1:
                        filter.type = Filter.Type.bill;
                        break;
                    case 2:
                        filter.type = Filter.Type.transaction;
                        break;
                    default:
                        filter.type = Filter.Type.all;
                }
            else
                isTypeInit = true;
        }
        else if (id == R.id.filter_sort_spinner){
            if (isSortInit)
                switch(position){
                    case 0:
                        filter.sort = Filter.Sort.high_to_low;
                        break;
                    case 1:
                        filter.sort = Filter.Sort.low_to_high;
                        break;
                    case 3:
                        filter.sort = Filter.Sort.oldest;
                        break;
                    case 2: default:
                        filter.sort = Filter.Sort.latest;
                }
            else
                isSortInit = true;
        }
    }

    public void setTypeSpinner(Filter.Type p){
        if (p == null){
            type.setSelection(0);
            return ;
        }
        switch (p){
            case bill:
                type.setSelection(1);
                break;
            case transaction:
                type.setSelection(2);
                break;
            case all: default:
                type.setSelection(0);
        }
    }

    public void setSortSpinner(Filter.Sort a){
        if (a == null){
            sort.setSelection(2);
            return ;
        }
        switch (a){
            case high_to_low:
                sort.setSelection(0);
                break;
            case low_to_high:
                sort.setSelection(1);
                break;
            case oldest:
                sort.setSelection(3);
                break;
            case latest: default:
                sort.setSelection(2);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /*do nothing*/
    }
}
