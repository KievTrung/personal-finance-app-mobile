package com.example.personalfinance.fragment.transaction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entity.Transact;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.category.CategoryFragment;
import com.example.personalfinance.fragment.category.model.CategoryModel;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.transaction.model.TransactModel;
import com.example.personalfinance.fragment.transaction.viewmodel.TransactionViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;

public class NewTransactionFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";

    private MainActivity activity;
    private TransactionViewModel viewModel;
    private String header;

    private EditText title, amount, description;
    private TextView currencyTv;
    private Button categoryBtn, itemBtn, createBtn;
    private ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view model
        viewModel = new ViewModelProvider(NewTransactionFragment.this).get(TransactionViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_transaction, container, false);
        init(v);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        activity.configToolbarToReturn(view -> {
            //return back
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you really want to quit " + TransactionViewModel.action + "? \nChanges will not be saved !" );
            dialog.setNoticeDialogListener(dialog1 -> {
                TransactionViewModel.tempTransact = new TransactModel();
                dialog1.dismiss();
                getParentFragmentManager().popBackStack();
            });
            dialog.show(getParentFragmentManager(), TAG);
        }) ;

        //set up bill fragment if user choose to create bill
        if (TransactionViewModel.tempTransact.getType() == Transact.Type.bill){
            itemBtn.setVisibility(View.VISIBLE);
            setTextForAmountEditText(TransactionViewModel.tempTransact.totalItemPrice());
            amount.setEnabled(false);
        }
        else{
            amount.setEnabled(true);
            itemBtn.setVisibility(View.GONE);
        }

        //config fragment arrcording to action
        switch (TransactionViewModel.action){
            case update:
                title.setText(TransactionViewModel.tempTransact.getTran_title());
                setTextForAmountEditText(TransactionViewModel.tempTransact.getTran_amount());
                description.setText(TransactionViewModel.tempTransact.getTran_description());
                displayCategory(TransactionViewModel.tempTransact.getCategoryModel());
                createBtn.setText("Save");
                amount.setEnabled(false);
                categoryBtn.setEnabled(false);
                header = (TransactionViewModel.tempTransact.getType() == Transact.Type.transaction) ? "Update transaction" : "Update bill";
                break;
            case insert:
                header = (TransactionViewModel.tempTransact.getType() == Transact.Type.transaction) ? "New transaction" : "New bill";
        }
        //constraint amount edittext
        MainActivity.setMaxDecimalInEditText(amount, 2);
        //set currency for text view
        viewModel.compositeDisposable.add(
                viewModel
                        .getCurrency()
                        .subscribe(currency -> {
                            //set currency
                            TransactionViewModel.currency = currency;

                            currencyTv.setText(currency.toString());
                            //turn off decimal if currency is vnd
                            if (currency == Currency.vnd) amount.setInputType(InputType.TYPE_CLASS_NUMBER);
                            else amount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        })
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(View v){
        activity = (MainActivity)getActivity();

        //set header
        activity.setToolBarHeaderText(header);

        //set up component
        title = v.findViewById(R.id.transact_title_et);
        amount = v.findViewById(R.id.transact_amount_et);
        description = v.findViewById(R.id.transact_description_et);

        currencyTv = v.findViewById(R.id.transact_currency);

        createBtn = v.findViewById(R.id.transact_create_btn);
        createBtn.setOnClickListener(this);

        categoryBtn = v.findViewById(R.id.transact_category_btn);
        categoryBtn.setOnClickListener(this);
        imageView = v.findViewById(R.id.transact_category_img);
        displayCategory(TransactionViewModel.tempTransact.getCategoryModel());

        itemBtn = v.findViewById(R.id.transact_view_item_btn);
        itemBtn.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String printDate(LocalDateTime localDateTime){
        if (localDateTime == null) localDateTime = LocalDateTime.now();
        int date = localDateTime.getDayOfMonth();
        int month = localDateTime.getMonthValue();
        int year = localDateTime.getYear();
        int hourOfDay = localDateTime.getHour();
        int minute = localDateTime.getMinute();

        return "Date picked: " + date + "/" + month + "/" + year + ", " + hourOfDay + ":" + minute;
    }

    public void displayCategory(CategoryModel categoryModel){
        if (categoryModel == null){
            categoryBtn.setText("pick category");
            imageView.setVisibility(View.GONE);
            return ;
        }
        else
            imageView.setVisibility(View.VISIBLE);

        categoryBtn.setText(categoryModel.getName());
        switch(categoryModel.getCategoryType()){
            case earning:
                categoryBtn.setText(categoryModel.getName() + " (earning category)");
                imageView.setImageResource(R.drawable.money_earn);
                break;
            case spending:
                categoryBtn.setText(categoryModel.getName() + " (spending category)");
                imageView.setImageResource(R.drawable.money_spend);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.transact_category_btn){
            activity.replaceFragment(new CategoryFragment(),true, null);

            //retrieve the choosen category for this transaction
            getParentFragmentManager().setFragmentResultListener("category", this, (requestKey, result) -> {
                CategoryModel categoryModel = (CategoryModel) result.getSerializable("payload");
                //set category for new transact
                TransactionViewModel.tempTransact.setCategoryModel(categoryModel);
                displayCategory(categoryModel);
            });
        }
        else if (id == R.id.transact_create_btn){
           //this is where to create new or udpate transact and return back to caller fragment
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Confirm " + ((TransactionViewModel.action == TransactionViewModel.Action.insert) ? "create ? " : "update ?"));
            dialog.setNoticeDialogListener(this::onConfirmCreateClick);
            dialog.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.transact_view_item_btn){
            //add or view item of bill
            if (TransactionViewModel.tempTransact.getItems() == null)
                TransactionViewModel.tempTransact.setItems(new ArrayList<>());
            activity.replaceFragment(new BillItemFragment(),true, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onConfirmCreateClick(DialogFragment dialogFragment) {
        String tran_title = title.getText().toString().trim();
        String tran_description = description.getText().toString().trim();
        String tran_amount = amount.getText().toString().trim().replace(",", "");

        //check title, amount, description
        if (!activity.checkTitle(tran_title)
                || !activity.checkAmount(tran_amount, TransactionViewModel.currency)
                || !activity.checkDescription(tran_description))
            return;

       //check category
        if (TransactionViewModel.tempTransact.getCategoryModel() == null){
            Toast.makeText(getContext(), MessageCode.category_required, Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }

        //check item size if it is bill
        if (TransactionViewModel.tempTransact.getType() == Transact.Type.bill
                && (TransactionViewModel.tempTransact.getItems() == null || TransactionViewModel.tempTransact.getItems().isEmpty())){
            Toast.makeText(getContext(), MessageCode.bill_missing_item, Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }

        TransactionViewModel.tempTransact.setTran_title(tran_title);
        TransactionViewModel.tempTransact.setTran_amount(Double.parseDouble(tran_amount));
        TransactionViewModel.tempTransact.setTran_description(tran_description);
        TransactionViewModel.tempTransact.setDate_time(LocalDateTime.now());

        /*pass*/
        switch(TransactionViewModel.action){
            case insert:
                insertTransact();
                break;
            case update:
                updateTransact();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertTransact(){
        viewModel.compositeDisposable.add(
                viewModel
                        .getWalletAmount(TransactionViewModel.tempTransact.getWallet_id())
                        .flatMapCompletable(amount -> {
                            //check if category is spending then wallet amount must be sufficient
                            if (TransactionViewModel.tempTransact.getCategoryModel().getCategoryType() == CategoryType.spending
                                    && amount < TransactionViewModel.tempTransact.getTran_amount())
                                throw new Exception(MessageCode.wallet_amount_not_sufficient);
                            return Completable.complete();
                        })
                        //if constraint is good then insert transact and update wallet
                        .andThen(viewModel.insertTransact(TransactionViewModel.tempTransact))
                        .subscribe(() -> {
                            Toast.makeText(requireContext(), MessageCode.success_creation, Toast.LENGTH_LONG).show();
                            /*pass*/
                            //check for budget exceeding
                            postNotificationIfExceededBudget(TransactionViewModel.tempTransact);
                            //reset and return back to caller fragment
                            TransactionViewModel.tempTransact = new TransactModel();
                            getParentFragmentManager().popBackStack();
                        }, throwable -> {
                            /*error*/
                            Toast.makeText(requireContext(), MessageCode.fail_creation, Toast.LENGTH_SHORT).show();
                            Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onConfirmCreateClick: " + throwable.getMessage());
                        })
        );
    }

    @SuppressLint("CheckResult")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postNotificationIfExceededBudget(TransactModel transactModel){
            viewModel.postNotificationIfExceededBudget(transactModel).subscribe(notifyObjects -> {
                activity.showNotification(notifyObjects);
            }, throwable -> {
                Log.d(TAG, "postNotificationIfExceededBudget: " + throwable.getMessage());
            });
    }

    public void updateTransact(){
        viewModel.compositeDisposable.add(
                viewModel.updateTransact(TransactionViewModel.tempTransact)
                .subscribe(() -> {
                    Toast.makeText(requireContext(), MessageCode.success_creation, Toast.LENGTH_LONG).show();
                    /*pass*/
                    //reset and return back to caller fragment
                    TransactionViewModel.tempTransact = new TransactModel();
                    getParentFragmentManager().popBackStack();
                }, throwable -> {
                    /*error*/
                    Toast.makeText(requireContext(), MessageCode.fail_updation, Toast.LENGTH_SHORT).show();
                    Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onConfirmCreateClick: " + throwable.getMessage());
                })
        );
    }

    private void setTextForAmountEditText(Double amount) {
        viewModel.compositeDisposable.add(
                viewModel
                        .getCurrency()
                        .subscribe(currency -> {
                            this.amount.setText(UserRepository.formatNumber(UserRepository.toCurrency(amount, currency), false, currency));
                        })
        );
    }

    @Override
    public void onResume() {
        activity.setToolBarHeaderText(header);
        super.onResume();
    }
}