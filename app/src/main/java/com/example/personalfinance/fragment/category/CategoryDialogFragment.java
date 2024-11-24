package com.example.personalfinance.fragment.category;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.CategoryType;

public class CategoryDialogFragment extends DialogFragment{
    private static final String TAG = "kiev";
    private PositiveDialogListener positive;
    private NeutralDialogListener neutral;
    private PositiveUpdateDialogListener positiveUpdate;
    private EditText editText;
    private TextView textView;

    enum Action{insert, update}

    static CategoryDialogFragment newInstance(Action action, CategoryModel categoryModel){
        CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("action", action.name());
        bundle.putSerializable("category", categoryModel);
        categoryDialogFragment.setArguments(bundle);
        return categoryDialogFragment;
    }

    public interface PositiveDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, CategoryModel categoryModel);
    }

    public interface NeutralDialogListener {
        void onDialogNeutralClick(DialogFragment dialog, CategoryModel categoryModel);
    }

    public interface PositiveUpdateDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, CategoryModel categoryModel);
    }

    public void setPositive(PositiveDialogListener positive) {
        this.positive = positive;
    }

    public void setNeutral(NeutralDialogListener neutral) {
        this.neutral = neutral;
    }

    public void setPositiveUpdate(PositiveUpdateDialogListener positive) {
        this.positiveUpdate = positive;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get action from bundle
        Action action = Action.valueOf(getArguments().getString("action"));

        View v = requireActivity().getLayoutInflater().inflate(R.layout.dialog_new_category, null);

        editText = v.findViewById(R.id.editText_category_title);
        textView = v.findViewById(R.id.textView_category_title);

        builder.setView(v)
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        switch (action){
            case update:
                CategoryModel updateCategory = (CategoryModel) getArguments().getSerializable("category");
                builder.setTitle("Update category")
                        .setPositiveButton("Save", (dialog, which) -> {
                            Log.d(TAG, "onCreateDialog: category " + updateCategory + ", title: " + editText.getText().toString());
                            updateCategory.setName(editText.getText().toString());
                            positiveUpdate.onDialogPositiveClick(CategoryDialogFragment.this, updateCategory);
                        })
                        .setNeutralButton("Delete", (dialog, which) -> neutral.onDialogNeutralClick(this, updateCategory));
                textView.setVisibility(View.VISIBLE);
                textView.setText("Old title: " + updateCategory.getName());
                break;
            case insert:
                builder.setTitle("New category")
                        .setPositiveButton("Save", (dialog, id) -> {
                            CategoryModel newCategory = new CategoryModel();
                            newCategory.setName(editText.getText().toString());
                            positive.onDialogPositiveClick(CategoryDialogFragment.this, newCategory);
                        });
                textView.setVisibility(View.GONE);
        }

        return builder.create();
    }

}
