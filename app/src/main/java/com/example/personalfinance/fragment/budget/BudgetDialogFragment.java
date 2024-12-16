package com.example.personalfinance.fragment.budget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.budget.model.BudgetModel;
import com.example.personalfinance.fragment.budget.viewmodel.BudgetViewModel;
import com.example.personalfinance.fragment.category.CategoryFragment;
import com.example.personalfinance.fragment.category.adapter.CategoryRecyclerViewAdapter;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.dialog.DateTimePickerDialogFragment;
import com.example.personalfinance.fragment.transaction.NewTransactionFragment;
import com.example.personalfinance.fragment.transaction.model.Filter;

import java.time.LocalDateTime;

public class BudgetDialogFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "kiev";
    EditText title, amount;
    TextView currencyTv;
    Spinner type;
    Button from, to, pickCategory;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    CategoryRecyclerViewAdapter adapter;
    BudgetModel budget;
    Currency currency;
    private BudgetViewModel viewModel;
    Filter filter;

    public BudgetDialogFragment(BudgetModel budget, Currency currency, Filter filter){
        this.budget = budget;
        this.currency = currency;
        this.filter = filter;
    }

    public interface ConfirmAddDialogListener{
        void onConFirmClick(DialogInterface dialog, BudgetModel budget);
    }

    public interface ConfirmFilterDialogListener{
        void onConFirmClick(DialogInterface dialog, Filter filter);
    }

    ConfirmAddDialogListener listener;
    ConfirmFilterDialogListener filterListenter;

    public void setFilterListenter(ConfirmFilterDialogListener filterListenter) {
        this.filterListenter = filterListenter;
    }

    public void setListener(ConfirmAddDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //set layout
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = requireActivity().getLayoutInflater().inflate(R.layout.dialog_new_budget, null);

        //init view model
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        title = v.findViewById(R.id.budget_title);
        linearLayout = v.findViewById(R.id.linearlayout);
        amount = v.findViewById(R.id.budget_amount_et);
        currencyTv = v.findViewById(R.id.budget_currency);
        type = v.findViewById(R.id.budget_category_type);
        from = v.findViewById(R.id.budget_begin_date);
        to = v.findViewById(R.id.budget_end_date);

        pickCategory = v.findViewById(R.id.budget_category);
        pickCategory.setOnClickListener(this);

        //set up adapter
        adapter = new CategoryRecyclerViewAdapter();
        adapter.setItemOnClickListener(position -> {/*do nothing*/});
        adapter.setItemOnLongClickListener(position -> {
            //delete item
            if (BudgetViewModel.action == BudgetViewModel.Action.update){
                if (budget.getCategories().size() == 1){
                    Toast.makeText(requireContext(), MessageCode.category_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Delete this category will be save instancely\nDo you still want to proceed ?");
                dialog.setNoticeDialogListener(dialog1 -> {
                    viewModel.compositeDisposable.add(
                            viewModel
                                    .deleteCategory(budget.getId(), budget.getCategories().get(position).getId())
                                    .subscribe(() -> {
                                        budget.removeCategory(position);
                                        adapter.update(budget.getCategories());
                                        Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_SHORT).show();
                                    })
                    );
                });
                dialog.show(getParentFragmentManager(), TAG);
            }
            else{
                if (BudgetViewModel.action == BudgetViewModel.Action.insert){
                    budget.removeCategory(position);
                    adapter.update(budget.getCategories());
                }
                else{
                    filter.categories.remove(position);
                    adapter.update(filter.categories);
                }
                Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_SHORT).show();
            }
        });

        if (BudgetViewModel.action != BudgetViewModel.Action.filter){
            linearLayout.setVisibility(View.VISIBLE);

            title.setText(budget.getBudget_title());

            if (budget.getBudget_amount() != null)
                amount.setText(UserRepository.formatNumber(UserRepository.toCurrency(budget.getBudget_amount(), currency), false, currency));

            //constraint amount edittext
            MainActivity.setMaxDecimalInEditText(amount, 2);
            if (currency == Currency.vnd) amount.setInputType(InputType.TYPE_CLASS_NUMBER);
            else amount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

            currencyTv.setText(currency.toString());

            type.setOnItemSelectedListener(this);
            setTypeSpinner(budget.getType());

            from.setOnClickListener(this);
            from.setText(NewTransactionFragment.printDate(budget.getStart_date()));

            to.setOnClickListener(this);
            to.setText(NewTransactionFragment.printDate(budget.getEnd_date()));

            adapter.update(budget.getCategories());
        }
        //filter model
        else {
            adapter.update(filter.categories);
            linearLayout.setVisibility(View.GONE);
            title.setText(filter.title);
        }

        //init recycler view
        recyclerView = v.findViewById(R.id.recycler_view_add_budget);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        //set dialog builder
        builder.setView(v)
                .setTitle((BudgetViewModel.action == BudgetViewModel.Action.insert) ? "New budget" : ((BudgetViewModel.action == BudgetViewModel.Action.filter) ? "Filter budget" : "Update budget"))
                .setPositiveButton((BudgetViewModel.action == BudgetViewModel.Action.filter) ? "Filter" : "Save" , (dialog, i) -> {
                    saveTitleAndAmount();
                    if (BudgetViewModel.action == BudgetViewModel.Action.filter)
                        filterListenter.onConFirmClick(dialog, filter);
                    else
                        listener.onConFirmClick(dialog, budget);
                })
                .setNegativeButton("Cancel", (dialog, i) -> dismiss());

        if (BudgetViewModel.action == BudgetViewModel.Action.filter)
            builder.setNeutralButton("Reset", (dialog, i) -> filterListenter.onConFirmClick(dialog, new Filter()));

        return builder.create();
    }

    public void saveTitleAndAmount(){
        if (BudgetViewModel.action != BudgetViewModel.Action.filter){
            budget.setBudget_title(title.getText().toString().trim());
            String amount_ = amount.getText().toString().trim().replace(",", "");
            budget.setBudget_amount((amount_.isEmpty()) ? null : UserRepository.backCurrency(Double.parseDouble(amount_), currency));
        }
        else
            filter.title = title.getText().toString().trim();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.budget_begin_date){
            DateTimePickerDialogFragment dialog = new DateTimePickerDialogFragment(budget.getStart_date());
            dialog.setDateTimeListener((dialog1, year, month, date1, hourOfDay, minute) -> {
                LocalDateTime temp = LocalDateTime.of(year, month, date1, hourOfDay, minute);
                if (temp.isAfter(budget.getEnd_date()))
                    Toast.makeText(requireContext(), MessageCode.from_date_field_constraint, Toast.LENGTH_SHORT).show();
                else{
                    budget.setStart_date(temp);
                    from.setText(NewTransactionFragment.printDate(budget.getStart_date()));
                }
            });
            dialog.show(getParentFragmentManager(), null);
        }
        else if (id == R.id.budget_end_date){
            DateTimePickerDialogFragment dialog = new DateTimePickerDialogFragment(budget.getEnd_date());
            dialog.setDateTimeListener((dialog1, year, month, date1, hourOfDay, minute) -> {
                LocalDateTime temp = LocalDateTime.of(year, month, date1, hourOfDay, minute);
                if (temp.isBefore(budget.getStart_date()))
                    Toast.makeText(requireContext(), MessageCode.to_date_field_constraint, Toast.LENGTH_SHORT).show();
                else{
                    budget.setEnd_date(temp);
                    to.setText(NewTransactionFragment.printDate(budget.getEnd_date()));
                }
            });
            dialog.show(getParentFragmentManager(), null);
        }
        else if (id == R.id.budget_category){
            saveTitleAndAmount();
            ((MainActivity)getActivity()).replaceFragment(new CategoryFragment(),true, null);
            BudgetViewModel.requestAddCategory = true;
            dismiss();
        }
    }

    private boolean isTypeInit = false;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isTypeInit){
            switch(position){
                case 1:
                    if (!budget.getCategories().isEmpty() && budget.getCategories().get(0).getCategoryType() == CategoryType.spending){
                        Toast.makeText(requireContext(), MessageCode.category_not_match_type + " all categories type is " + budget.getCategories().get(0).getCategoryType(), Toast.LENGTH_SHORT).show();
                        type.setSelection(0);
                        return;
                    }
                    budget.setType(CategoryType.earning);
                    break;
                case 0: default:
                    if (!budget.getCategories().isEmpty() && budget.getCategories().get(0).getCategoryType() == CategoryType.earning){
                        Toast.makeText(requireContext(), MessageCode.category_not_match_type + " all categories type is " + budget.getCategories().get(0).getCategoryType(), Toast.LENGTH_SHORT).show();
                        type.setSelection(1);
                        return;
                    }
                    budget.setType(CategoryType.spending);
            }
        }
        else
            isTypeInit = true;
    }

    private void setTypeSpinner(CategoryType p){
        if (p == null){
            type.setSelection(0);
            return ;
        }
        switch (p){
            case earning:
                type.setSelection(1);
                break;
            case spending: default:
                type.setSelection(0);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /*do nothing*/
    }
}
