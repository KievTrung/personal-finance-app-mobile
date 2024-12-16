package com.example.personalfinance.fragment.budget;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.budget.adapter.BudgetReyclerViewAdapter;
import com.example.personalfinance.fragment.budget.model.BudgetModel;
import com.example.personalfinance.fragment.budget.viewmodel.BudgetViewModel;
import com.example.personalfinance.fragment.category.model.CategoryModel;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.transaction.model.Filter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BudgetFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    FloatingActionButton fab, fab_filter, fab_add;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    private MainActivity activity;

    private BudgetReyclerViewAdapter adapter;
    private BudgetViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view model
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        //init adapter
        adapter = new BudgetReyclerViewAdapter(getParentFragmentManager());
        adapter.setItemOnClickListener(position -> {
            //update budget
            viewModel.budgetModel = viewModel.getBudgets().get(position);
            BudgetViewModel.action = BudgetViewModel.Action.update;
            useNewBudgetDialog();
        });
        adapter.setItemOnLongClickListener(position -> {
            //delete budget
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to delete this budget ?");
            dialog.setNoticeDialogListener(dialog1 -> {
                viewModel.compositeDisposable.add(
                    viewModel
                            .delete(viewModel.getBudgets().get(position))
                            .doOnComplete(() -> Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_LONG).show())
                            .subscribe(() -> fetchBudget(viewModel.filter))
                );
            });
            dialog.show(getParentFragmentManager(), TAG);
        });

        //get activity
        activity = (MainActivity)getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_budget, container, false);

        fab = v.findViewById(R.id.fab);
        fab_filter = v.findViewById(R.id.fab_filter);
        fab_add = v.findViewById(R.id.fab_add);

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close);
        rotateBackward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_forward);

        fab.setOnClickListener(this);
        fab_filter.setOnClickListener(this);
        fab_add.setOnClickListener(this);

        init(v);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(View v){
        //init recycler view
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view_budget);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        //respond to a budget exceeded notification
        getParentFragmentManager().setFragmentResultListener("budgetid", this, (requestKey, result) -> {
            viewModel.compositeDisposable.add(
                    viewModel.getBudget(result.getInt("payloadbudget")).subscribe(budgetModel -> {
                        BudgetTransactionDialogFragment dialog = new BudgetTransactionDialogFragment(budgetModel);
                        dialog.show(getParentFragmentManager(), TAG);
                    })
            );
            long notify_id = result.getLong("payloadnotify");
            Log.d(TAG, "init: " + notify_id);
            viewModel.compositeDisposable.add(
                    viewModel.hasRead((int)notify_id).subscribe(() -> {
                        Toast.makeText(requireContext(), "Notification has read", Toast.LENGTH_LONG).show();
                    })
            );
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        //set tool bar
        activity.setToolBarReturnBtnVisibility(View.INVISIBLE);
        activity.setToolBarHeaderText("My budgets");
        activity.setToolBarMenuBtnVisibility(View.VISIBLE);

        //check if there is a request to add category to new budget
        if (BudgetViewModel.requestAddCategory){
            BudgetViewModel.requestAddCategory = false;

            //retrieve the choosen category
            getParentFragmentManager().setFragmentResultListener("category", this, (requestKey, result) -> {
                CategoryModel categoryModel = (CategoryModel) result.getSerializable("payload");

                if (BudgetViewModel.action == BudgetViewModel.Action.update){
                    ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Add this category will be save instancely\nDo you still want to proceed ?");
                    dialog.setNoticeDialogListener(dialog1 -> {
                        try{
                            viewModel.budgetModel.addCategory(categoryModel);
                            viewModel.compositeDisposable.add(
                                    viewModel.insertCategory(viewModel.budgetModel.getId(), categoryModel.getId())
                                            .subscribe(() -> Toast.makeText(requireContext(), "Add category successfully", Toast.LENGTH_LONG).show())
                            );
                        }catch(RuntimeException e){
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        } finally {
                            useNewBudgetDialog();
                        }
                    });
                    dialog.setCancelListener(dialog1 -> useNewBudgetDialog());
                    dialog.show(getParentFragmentManager(), TAG);
                }
                else {
                    try{
                        if (BudgetViewModel.action == BudgetViewModel.Action.insert)
                            viewModel.budgetModel.addCategory(categoryModel);
                        else //filte
                            viewModel.filter.addCategory(categoryModel);
                        Toast.makeText(requireContext(), "Add category successfully", Toast.LENGTH_LONG).show();
                    }catch(RuntimeException e){
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        useNewBudgetDialog();
                    }
                }
            });
        }
        else
            fetchBudget(viewModel.filter);

        //get currency
        viewModel.compositeDisposable.add(viewModel.getCurrency().subscribe(currency -> BudgetViewModel.currency = currency));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchBudget(Filter filter){
        //fetch all budget from local
        viewModel.compositeDisposable.add(
                viewModel.fetch(filter).subscribe(budgetModels -> adapter.update(budgetModels))
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fab)
            animateFab();
        else if (id == R.id.fab_add){
            animateFab();
            viewModel.budgetModel = new BudgetModel();
            BudgetViewModel.action = BudgetViewModel.Action.insert;
            useNewBudgetDialog();
        }
        else if (id == R.id.fab_filter){
            animateFab();
            BudgetViewModel.action = BudgetViewModel.Action.filter;
            useNewBudgetDialog();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void useNewBudgetDialog(){
        //before firing new budget dialog, have to get the currency
        BudgetDialogFragment dialog = new BudgetDialogFragment(viewModel.budgetModel, BudgetViewModel.currency, viewModel.filter);
        dialog.setListener(this::onConFirmAddingClick);
        dialog.setFilterListenter(this::onConfirmFilterClick);
        dialog.show(getParentFragmentManager(), TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onConfirmFilterClick(DialogInterface dialogInterface, Filter filter) { fetchBudget(filter);}

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onConFirmAddingClick(DialogInterface dialog, BudgetModel budget){
       //add new budget
        //check budget info
        if (!activity.checkTitle(budget.getBudget_title())
                || !activity.checkAmount(budget.getBudget_amount()==null ? "" : String.valueOf(budget.getBudget_amount()), BudgetViewModel.currency)){
            useNewBudgetDialog();
        }
        else if (budget.getCategories().isEmpty()){
            Toast.makeText(getContext(), MessageCode.category_required, Toast.LENGTH_LONG).show();
            useNewBudgetDialog();
        }
        else{
            /*pass*/
            executeAction();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void executeAction(){
        ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you confirm ?");
        dialog.setNoticeDialogListener(dialog1 -> {
            switch(BudgetViewModel.action){
                case insert:
                    insertBudget();
                    break;
                case update:
                    updateBudget();
            }
        });
        dialog.setCancelListener(dialog1 -> useNewBudgetDialog());
        dialog.show(getParentFragmentManager(), TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void insertBudget() {
        viewModel.compositeDisposable.add(
                viewModel.insert(viewModel.budgetModel)
                        .subscribe(() -> {
                            Toast.makeText(requireContext(), MessageCode.success_creation, Toast.LENGTH_LONG).show();
                            /*pass*/
                            viewModel.budgetModel = new BudgetModel();
                            viewModel.filter = new Filter();
                            fetchBudget(viewModel.filter);
                        }, throwable -> {
                            /*error*/
                            Toast.makeText(requireContext(), MessageCode.fail_creation, Toast.LENGTH_SHORT).show();
                            if (throwable.getLocalizedMessage().contains("UNIQUE"))
                                Toast.makeText(requireContext(), MessageCode.field_title_duplicated, Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(requireContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onConfirmCreateClick: " + throwable.getMessage());
                            useNewBudgetDialog();
                        })
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateBudget() {
        viewModel.compositeDisposable.add(
                viewModel.update(viewModel.budgetModel)
                        .subscribe(() -> {
                            Toast.makeText(requireContext(), MessageCode.success_updation, Toast.LENGTH_LONG).show();
                            /*pass*/
                            fetchBudget(viewModel.filter);
                        }, throwable -> {
                            /*error*/
                            Toast.makeText(requireContext(), MessageCode.fail_updation, Toast.LENGTH_SHORT).show();
                            Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onConfirmCreateClick: " + throwable.getMessage());
                        })
        );
    }

    public void animateFab(){
        if (isOpen){
            fab.startAnimation(rotateForward);
            fab_add.startAnimation(fabClose);
            fab_filter.startAnimation(fabClose);
            fab_add.setClickable(false);
            fab_filter.setClickable(false);
            isOpen = false;
        }
        else{
            fab.startAnimation(rotateBackward);
            fab_add.startAnimation(fabOpen);
            fab_filter.startAnimation(fabOpen);
            fab_add.setClickable(true);
            fab_filter.setClickable(true);
            isOpen = true;
        }
    }

}