package com.example.personalfinance.fragment.category;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.personalfinance.R;
import com.example.personalfinance.fragment.category.model.CategoryModel;

public class CategoryDialogFragment extends DialogFragment{
    private static final String TAG = "kiev";
    private PositiveDialogListener positive;
    private NeutralDialogListener neutral;
    private PositiveUpdateDialogListener positiveUpdate;
    private EditText editText;
    private TextView textView;
    private Action action;
    private CategoryModel categoryModel;

    enum Action{insert, update}

    public CategoryDialogFragment(Action action, CategoryModel categoryModel){
        this.action = action;
        this.categoryModel = categoryModel;
    }

    public interface PositiveDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, CategoryModel categoryModel);
    }

    public interface NeutralDialogListener {
        void onDialogNeutralClick(DialogFragment dialog, CategoryModel categoryModel);
    }

    public interface PositiveUpdateDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, CategoryModel categoryModel, String oldTitle);
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

        View v = requireActivity().getLayoutInflater().inflate(R.layout.dialog_new_category, null);

        editText = v.findViewById(R.id.editText_category_title);
        textView = v.findViewById(R.id.textView_category_title);

        builder.setView(v)
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        switch (action){
            case update:
                String oldTitle = categoryModel.getName();
                builder.setTitle("Update category")
                        .setPositiveButton("Save", (dialog, which) -> {
                            categoryModel.setName(editText.getText().toString());
                            positiveUpdate.onDialogPositiveClick(CategoryDialogFragment.this, categoryModel, oldTitle);
                        })
                        .setNeutralButton("Delete", (dialog, which) -> neutral.onDialogNeutralClick(this, categoryModel));
                textView.setVisibility(View.VISIBLE);
                textView.setText("Old title: " + oldTitle);
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
