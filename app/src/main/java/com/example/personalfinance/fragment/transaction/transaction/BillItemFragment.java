package com.example.personalfinance.fragment.transaction.transaction;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.transaction.transaction.adapter.ItemRecyclerViewAdapter;
import com.example.personalfinance.fragment.transaction.transaction.model.ItemModel;

public class BillItemFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    private TransactionViewModel viewModel;
    private RecyclerView recyclerView;
    private ItemRecyclerViewAdapter adapter;
    private MainActivity activity;

    private enum Action{ insert, update }
    private Action action;
    private Integer position;

    private EditText title, amount, quantity;
    private TextView currencyTv;
    private Button addBtn, cancelBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init view model
        viewModel = new ViewModelProvider(BillItemFragment.this).get(TransactionViewModel.class);

        //init adapter
        adapter = new ItemRecyclerViewAdapter();
        //if there something inside list, then display it
        adapter.update(TransactionViewModel.tempTransact.getItems());
        adapter.setItemOnClickListener(position -> {
            //for update item
            ItemModel item = TransactionViewModel.tempTransact.getItems().get(position);

            title.setText(item.getItem_name());
            viewModel.compositeDisposable.add(
                    viewModel
                            .getCurrency()
                            .subscribe(currency -> {
                                amount.setText(UserRepository.formatNumber(UserRepository.toCurrency(item.getItem_price(), currency), false, currency));
                            })
            );
            quantity.setText(String.valueOf(item.getQuantity()));

            updateMode();
            this.position = position;
        });
        adapter.setItemOnLongClickListener(position -> {
            //for delete item only for inserting item
            if (TransactionViewModel.action == TransactionViewModel.Action.insert){
                ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to delete this item ?");
                dialog.setNoticeDialogListener(dialog1 -> {
                    Toast.makeText(requireContext(), "Deleting successfully", Toast.LENGTH_LONG).show();
                    TransactionViewModel.tempTransact.removeItem(position);
                    adapter.update(TransactionViewModel.tempTransact.getItems());
                    dialog1.dismiss();
                });
                dialog.show(getParentFragmentManager(), TAG);
            }
        });
        activity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bill_item, container, false);
        init(v);
        return v;
    }

    private void init(View v){

        //init recycler view
        recyclerView = v.findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //set currency before assign adapter
        viewModel.compositeDisposable.add(
                viewModel.getCurrency().subscribe(currency -> {
                    adapter.setCurrency(currency);
                    recyclerView.setAdapter(adapter);
        }));

        //set up component
        title = v.findViewById(R.id.et_item_title);
        amount = v.findViewById(R.id.et_item_amount);
        currencyTv = v.findViewById(R.id.item_currency_tv);
        quantity = v.findViewById(R.id.et_item_quantity);
        addBtn = v.findViewById(R.id.add_item_btn);
        addBtn.setOnClickListener(this);
        cancelBtn = v.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(v1 -> insertMode());

        if (TransactionViewModel.action == TransactionViewModel.Action.update){
            activity.setToolBarHeaderText("Update items");
            title.setEnabled(false);
            amount.setEnabled(false);
            quantity.setEnabled(false);
        }
        else{
            activity.setToolBarHeaderText("Add items");
        }
        insertMode();

    }

    public void insertMode(){
        if (TransactionViewModel.action == TransactionViewModel.Action.update){
            title.setEnabled(false);
            addBtn.setVisibility(View.GONE);
        }
        cancelBtn.setVisibility(View.GONE);
        addBtn.setText("add item");
        resetEditText();
        action = Action.insert;
    }

    public void updateMode(){
        if (TransactionViewModel.action == TransactionViewModel.Action.update)
            title.setEnabled(true);
        addBtn.setText("Save");
        addBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        action = Action.update;
    }

    public void resetEditText(){
        title.setText("");
        amount.setText("");
        quantity.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        //constraint amount edittext
        MainActivity.setMaxDecimalInEditText(amount, 2);
        //set up tool bar return btn
        activity.configToolbarToReturn(view -> getParentFragmentManager().popBackStack());
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

    @Override
    public void onClick(View view) {
        //add item to list and display in recycler view

        ItemModel item = new ItemModel();
        String item_title = title.getText().toString().trim();
        String item_quantity = quantity.getText().toString().trim();
        String item_amount = amount.getText().toString().trim().replace(",", "");

        //check title
        try{
            if (item_title.isEmpty()) throw new Exception("Empty title, please try again");
            Double.parseDouble(item_title);
            Toast.makeText(getContext(), "Invalid title, please try again", Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }catch(NumberFormatException e){
            //check for duplication title
            if (action == Action.insert && !TransactionViewModel.tempTransact.isDuplicateTitle(item_title, null)
            || (action == Action.update && !TransactionViewModel.tempTransact.isDuplicateTitle(item_title, position))){
                /*pass*/
                item.setItem_name(item_title);
            }
            else{
                /*error*/
                Toast.makeText(requireContext(), "This item has existed, please try again", Toast.LENGTH_LONG).show();
                return;
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }
        //check amount
        try{
            Double amount = Double.parseDouble(item_amount);
            Integer quantity = Integer.parseInt(item_quantity);
            if (quantity < 1) throw new Exception("quantity must be greater than 0, please try again");
            /*pass*/
            item.setItem_price(amount);
            item.setQuantity(quantity);
        }catch (NumberFormatException e){
            Toast.makeText(getContext(), "Invalid amount or quantity, please try again", Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }
        catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }

        /*pass*/
        ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Confirm save ?");
        dialog.setNoticeDialogListener(dialog1 -> {
            //insert into list
            //get currency
            viewModel.compositeDisposable.add(
                    viewModel
                            .getCurrency()
                            .subscribe(currency -> {
                                if ((currency == Currency.vnd && item.getItem_price() == 0d)
                                        ||(currency == Currency.usd && item.getItem_price() == 0.00d)){
                                    /*error*/
                                    Toast.makeText(getContext(), "Amount must greater than 0, please try again", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                /*pass*/
                                item.setItem_price(UserRepository.backCurrency(item.getItem_price(), currency));
                                switch (action){
                                    case update:
                                        TransactionViewModel.tempTransact.replaceItem(position, item);
                                        break;
                                    case insert:
                                        TransactionViewModel.tempTransact.addItem(item);
                                }
                                adapter.update(TransactionViewModel.tempTransact.getItems());
                                Toast.makeText(requireContext(), "Saving successfully", Toast.LENGTH_LONG).show();
                                insertMode();
                                resetEditText();
                            })
            );
        });
        dialog.show(getParentFragmentManager(), TAG);
    }

}