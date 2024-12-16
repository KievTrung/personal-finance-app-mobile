package com.example.personalfinance.fragment.budget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.budget.model.BudgetModel;
import com.example.personalfinance.fragment.budget.viewmodel.BudgetViewModel;
import com.example.personalfinance.fragment.transaction.adapter.TransactionRecyclerViewAdapter;

import java.util.ArrayList;

public class BudgetTransactionDialogFragment extends DialogFragment {
    TextView currencyTv;
    RecyclerView recyclerView;
    TransactionRecyclerViewAdapter adapter;
    private BudgetViewModel viewModel;
    private final BudgetModel budgetModel;

    public BudgetTransactionDialogFragment(BudgetModel budgetModel){
        this.budgetModel = budgetModel;
    }

    @NonNull
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //set layout
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = requireActivity().getLayoutInflater().inflate(R.layout.dialog_budget_transaction, null);

        //init view model
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        currencyTv = v.findViewById(R.id.text_view_percentage);

        //set up adapter
        adapter = new TransactionRecyclerViewAdapter(new ArrayList<>());
        adapter.setItemOnClickListener(position -> {/*do nothing*/});
        adapter.setItemOnLongClickListener(position -> {/*do nothing*/});

        //init recycler view
        recyclerView = v.findViewById(R.id.recycler_view_budget_transact);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.compositeDisposable.add(
                viewModel.getAllTransact(budgetModel.getId()).subscribe(transactModels -> {
                    viewModel.compositeDisposable.add(viewModel.getCurrency().subscribe(currency -> {
                        //set text view
                        Double total = budgetModel.getTotalTransactsAmount();
                        String text = UserRepository.formatNumber(UserRepository.toCurrency((total == null) ? 0 : total ,currency), false, currency)
                                + " / "
                                + UserRepository.formatNumber(UserRepository.toCurrency(budgetModel.getBudget_amount() ,currency), true, currency);
                        currencyTv.setText(text);
                        //set recycler view
                        adapter.setCurrency(currency);
                        adapter.update(transactModels);
                        recyclerView.setAdapter(adapter);
                    }));
        }));

        builder.setView(v)
                .setTitle("Budget "+ budgetModel.getBudget_title() +" detail")
                .setPositiveButton("Exit" , (dialog, i) -> dismiss());

        return builder.create();
    }

}
