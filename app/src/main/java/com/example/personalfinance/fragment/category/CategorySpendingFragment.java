package com.example.personalfinance.fragment.category;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalfinance.R;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.category.adapter.CategoryRecyclerViewAdapter;
import com.example.personalfinance.fragment.category.model.CategoryModel;
import com.example.personalfinance.fragment.category.viewmodel.SpendingCategoryViewModel;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;

public class CategorySpendingFragment extends Fragment{
    private static final String TAG = "kiev ui";
    private RecyclerView recyclerView;
    public CategoryRecyclerViewAdapter adapter;
    private SpendingCategoryViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init view model
        viewModel = new ViewModelProvider(requireActivity()).get(SpendingCategoryViewModel.class);

        //init adapter
        adapter = new CategoryRecyclerViewAdapter();
        adapter.setItemOnClickListener(position -> {
            //return the seleted item back to caller fragment
            Bundle result = new Bundle();
            result.putSerializable("payload", viewModel.getSpendings().get(position));
            Fragment parent = getParentFragment();
            parent.getParentFragmentManager().setFragmentResult("category", result);
            parent.getParentFragmentManager().popBackStack();
        });
        adapter.setItemOnLongClickListener(position -> {
            //update and delete category
            //cancel update and delete if this category has any transaction or budget associating
            viewModel.compositeDisposable.add(
                    viewModel
                            .countTransactAndBudget(viewModel.getSpendings().get(position).getId())
                            .subscribe(count -> {
                                if (count != 0)
                                    Toast.makeText(requireContext(), MessageCode.use_category, Toast.LENGTH_LONG).show();
                                else {
                                    Log.d(TAG, "onCreate: " + viewModel.getSpendings().get(position));
                                    CategoryDialogFragment dialog = new CategoryDialogFragment(CategoryDialogFragment.Action.update, viewModel.getSpendings().get(position));
                                    dialog.setPositiveUpdate(this::onDialogPositiveUpdateClick);
                                    dialog.setNeutral(this::onDialogNeutralClick);
                                    dialog.show(getParentFragmentManager(), TAG);
                                }
                            })
            );
        });

        //fetch from local
        viewModel.compositeDisposable.add(
                viewModel.fetchCategory().subscribe(categoryModels -> adapter.update(categoryModels))
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: spending");
        View v = inflater.inflate(R.layout.fragment_spending, container, false);
        init(v);
        return v;
    }

    public void init(View v){
        //init recycler view
        recyclerView = v.findViewById(R.id.recycler_view_spending);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("CheckResult")
    public void onDialogPositiveUpdateClick(DialogFragment dialog, CategoryModel categoryModel, String oldTitle) {
        //update

        ConfirmDialogFragment confirm = ConfirmDialogFragment.newInstance("Confirm updating ?");

        confirm.setNoticeDialogListener(confirmDialog -> {
            viewModel.compositeDisposable.add(
                    viewModel.update(categoryModel)
                            .andThen(viewModel.fetchCategory())
                            .subscribe(categoryModels -> {
                                        Toast.makeText(requireContext(), MessageCode.success_updation, Toast.LENGTH_LONG).show();
                                        adapter.update(categoryModels);
                                    }
                                    , throwable -> {
                                        viewModel.setTitle(categoryModel.getId(), oldTitle);
                                        Toast.makeText(requireContext(), MessageCode.fail_updation, Toast.LENGTH_SHORT).show();
                                        if (throwable.getMessage().contains("UNIQUE"))
                                            Toast.makeText(requireContext(), MessageCode.category_duplicated, Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    })
            );
            confirmDialog.dismiss();
            dialog.dismiss();
        });
        confirm.show(getParentFragmentManager(), null);
    }

    @SuppressLint("CheckResult")
    public void onDialogNeutralClick(DialogFragment dialog, CategoryModel categoryModel) {
       //delete
        ConfirmDialogFragment confirm = ConfirmDialogFragment.newInstance("Confirm delete ?");

        confirm.setNoticeDialogListener((confirmDialog) -> {
            viewModel.compositeDisposable.add(
                    viewModel.delete(categoryModel)
                            .andThen(viewModel.fetchCategory())
                            .subscribe(categoryModels -> {
                                Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_LONG).show();
                                adapter.update(categoryModels);
                            }, throwable -> Log.d(TAG, "onError, delete category: " + throwable.getMessage()))
            );
            confirmDialog.dismiss();
            dialog.dismiss();
        });
        confirm.show(getParentFragmentManager(), null);
    }

}