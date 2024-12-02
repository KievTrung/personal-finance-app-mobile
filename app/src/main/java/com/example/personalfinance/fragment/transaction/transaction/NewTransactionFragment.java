package com.example.personalfinance.fragment.transaction.transaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.enums.Period;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;
import com.example.personalfinance.fragment.category.CategoryFragment;
import com.example.personalfinance.fragment.category.CategoryModel;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.dialog.DateTimePickerDialogFragment;
import com.example.personalfinance.fragment.transaction.PayLaterFragment;
import com.example.personalfinance.fragment.transaction.transaction.model.TransactModel;

import java.time.LocalDateTime;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;

public class NewTransactionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private static final String TAG = "kiev";

    private MainActivity activity;
    private TransactionViewModel viewModel;
    private String header;

    private EditText title, amount, description;
    private TextView currencyTv;
    private Button dateBtn, categoryBtn, itemBtn, payLaterBtn, createBtn;
    private ImageView imageView;
    private Spinner spinner;

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
                dateBtn.setText(printDate(TransactionViewModel.tempTransact.getDate_time()));
                displayCategory(TransactionViewModel.tempTransact.getCategoryModel());
                setSpinner(TransactionViewModel.tempTransact.getAuto_tran());
                createBtn.setText("Save");
                amount.setEnabled(false);
                categoryBtn.setEnabled(false);
                payLaterBtn.setEnabled(false);
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

        payLaterBtn = v.findViewById(R.id.transact_pay_later_btn);
        payLaterBtn.setOnClickListener(this);

        createBtn = v.findViewById(R.id.transact_create_btn);
        createBtn.setOnClickListener(this);

        categoryBtn = v.findViewById(R.id.transact_category_btn);
        categoryBtn.setOnClickListener(this);
        imageView = v.findViewById(R.id.transact_category_img);
        displayCategory(TransactionViewModel.tempTransact.getCategoryModel());

        dateBtn = v.findViewById(R.id.transact_date_btn);
        dateBtn.setOnClickListener(this);
        dateBtn.setText(printDate(TransactionViewModel.tempTransact.getDate_time()));

        itemBtn = v.findViewById(R.id.transact_view_item_btn);
        itemBtn.setOnClickListener(this);

        spinner = v.findViewById(R.id.transact_repeat_drop_down);
        spinner.setOnItemSelectedListener(this);
        setSpinner(TransactionViewModel.tempTransact.getAuto_tran());
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

    public void setSpinner(Period p){
        if (p == null){
            spinner.setSelection(0);
            return ;
        }
        switch (p){
            case DATE:
                spinner.setSelection(1);
                break;
            case MONTH:
                spinner.setSelection(2);
                break;
            case YEAR:
                spinner.setSelection(3);
                break;
            case NONE: default:
                spinner.setSelection(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.transact_pay_later_btn){
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Pay later ?");
            dialog.setNoticeDialogListener(this::onDialogPositiveClick);
            dialog.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.transact_category_btn){
            activity.replaceFragment(new CategoryFragment(),true, null);

            //retrieve the choosen category for this transaction
            getParentFragmentManager().setFragmentResultListener("category", this, (requestKey, result) -> {
                CategoryModel categoryModel = (CategoryModel) result.getSerializable("payload");
                //set category for new transact
                TransactionViewModel.tempTransact.setCategoryModel(categoryModel);
                displayCategory(categoryModel);
            });
        }
        else if (id == R.id.transact_date_btn){
            //pick date for transact
            DateTimePickerDialogFragment dialog = new DateTimePickerDialogFragment(TransactionViewModel.tempTransact.getDate_time());
            dialog.setDateTimeListener(this::onConfirmDateTimeClick);
            dialog.show(getParentFragmentManager(), TAG);
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

        //set date time if user not set
        if (TransactionViewModel.tempTransact.getDate_time() == null)
            TransactionViewModel.tempTransact.setDate_time(LocalDateTime.now());
        //set description
        TransactionViewModel.tempTransact.setTran_description(tran_description);
        //check title
        try{
            if (tran_title.isEmpty()) throw new Exception("Empty title, please try again");
            Double.parseDouble(tran_title);
            Toast.makeText(getContext(), "Invalid title, please try again", Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }catch(NumberFormatException e){
            /*pass*/
            TransactionViewModel.tempTransact.setTran_title(tran_title);
        }
        catch (Exception e){
            /*error*/
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        //check amount
        try{
            double amount = Double.parseDouble(tran_amount);
            /*pass*/
            TransactionViewModel.tempTransact.setTran_amount(amount);
        }catch (NumberFormatException e){
            Toast.makeText(getContext(), "Invalid amount, please try again", Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }
        catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }
        //check date
        if (TransactionViewModel.tempTransact.getDate_time().isAfter(LocalDateTime.now())){
            /*error*/
            Toast.makeText(getContext(), "The date must be before now, please try again", Toast.LENGTH_LONG).show();
            return;
        }
       //check category
        if (TransactionViewModel.tempTransact.getCategoryModel() == null){
            Toast.makeText(getContext(), "Please pick category", Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }
        //check item size if it is bill
        if (TransactionViewModel.tempTransact.getType() == Transact.Type.bill
                && (TransactionViewModel.tempTransact.getItems() == null || TransactionViewModel.tempTransact.getItems().isEmpty())){
            Toast.makeText(getContext(), "Please add item to this bill", Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }

        viewModel.compositeDisposable.add(
                viewModel
                        .getCurrency()
                        .subscribe(currency -> {
                            if ((currency == Currency.vnd && TransactionViewModel.tempTransact.getTran_amount() == 0d)
                                    ||(currency == Currency.usd && TransactionViewModel.tempTransact.getTran_amount() == 0.00d)){
                                /*error*/
                                Toast.makeText(getContext(), "Amount must greater than 0, please try again", Toast.LENGTH_LONG).show();
                                return;
                            }
                            /*pass*/
                            switch(TransactionViewModel.action){
                                case insert:
                                    insertTransact();
                                    break;
                                case update:
                                    updateTransact();
                            }
                        })
                );
    }

    public void insertTransact(){
        viewModel.compositeDisposable.add(
                viewModel
                        .getWalletAmount(TransactionViewModel.tempTransact.getWallet_id())
                        .flatMapCompletable(amount -> {
                            //check if category is spending then wallet amount must be sufficient
                            if (TransactionViewModel.tempTransact.getCategoryModel().getCategoryType() == CategoryType.spending
                                    && amount < TransactionViewModel.tempTransact.getTran_amount())
                                throw new Exception("Wallet amount not sufficient to create transaction !");
                            return Completable.complete();
                        })
                        //if constraint is good then insert transact and update wallet
                        .andThen(viewModel.insertTransact(TransactionViewModel.tempTransact))
                        .subscribe(() -> {
                            Toast.makeText(requireContext(), "Creating successfully", Toast.LENGTH_LONG).show();
                            /*pass*/
                            //reset and return back to caller fragment
                            TransactionViewModel.tempTransact = new TransactModel();
                            getParentFragmentManager().popBackStack();
                        }, throwable -> {
                            /*error*/
                            Toast.makeText(requireContext(), "Creating failed", Toast.LENGTH_SHORT).show();
                            Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onConfirmCreateClick: " + throwable.getMessage());
                        })
        );
    }

    public void updateTransact(){
        viewModel.compositeDisposable.add(
                viewModel.updateTransact(TransactionViewModel.tempTransact)
                .subscribe(() -> {
                    Toast.makeText(requireContext(), "Update successfully", Toast.LENGTH_LONG).show();
                    /*pass*/
                    //reset and return back to caller fragment
                    TransactionViewModel.tempTransact = new TransactModel();
                    getParentFragmentManager().popBackStack();
                }, throwable -> {
                    /*error*/
                    Toast.makeText(requireContext(), "Updating failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onConfirmCreateClick: " + throwable.getMessage());
                })
        );
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        //todo: implement borrow money fearture
        activity.replaceFragment(new PayLaterFragment(), true, null);
    }

    //set date
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onConfirmDateTimeClick(DialogInterface dialog, int year, int month, int date, int hourOfDay, int minute){
        dateBtn.setText("Date picked: " + date + "/" + month + "/" + year + ", " + hourOfDay + ":" + minute);
        TransactionViewModel.tempTransact.setDate_time(LocalDateTime.of(year, month, date, hourOfDay, minute));
        dialog.dismiss();
    }

    private boolean isSpinnerInit = false;

    //set period
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isSpinnerInit)
            switch(position){
                case 1:
                    TransactionViewModel.tempTransact.setAuto_tran(Period.DATE);
                    break;
                case 2:
                    TransactionViewModel.tempTransact.setAuto_tran(Period.MONTH);
                    break;
                case 3:
                    TransactionViewModel.tempTransact.setAuto_tran(Period.YEAR);
                    break;
                default:
                    TransactionViewModel.tempTransact.setAuto_tran(Period.NONE);
            }
        else
            isSpinnerInit = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /*do nothing*/
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