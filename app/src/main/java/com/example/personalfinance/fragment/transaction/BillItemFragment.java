package com.example.personalfinance.fragment.transaction;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.transaction.transaction.TransactionViewModel;
import com.example.personalfinance.fragment.transaction.transaction.adapter.ItemRecyclerViewAdapter;
import com.example.personalfinance.fragment.transaction.transaction.model.ItemModel;

public class BillItemFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    private TransactionViewModel viewModel;
    private RecyclerView recyclerView;
    private ItemRecyclerViewAdapter adapter;
    private MainActivity activity;

    private EditText title, amount, quantity;
    private Button addBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init view model
        viewModel = new ViewModelProvider(BillItemFragment.this).get(TransactionViewModel.class);

        //init adapter
        adapter = new ItemRecyclerViewAdapter();
        adapter.setItemOnClickListener(position -> {
            //for update item

        });
        adapter.setItemOnLongClickListener(position -> {
            Log.d(TAG, "click: invoked");
            //for delete item
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to delete this item ?");
            dialog.setNoticeDialogListener(dialog1 -> {
                TransactionViewModel.tempTransact.removeItem(position);
                adapter.update(TransactionViewModel.tempTransact.getItems());
                dialog1.dismiss();
            });
            dialog.show(getParentFragmentManager(), TAG);
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
        recyclerView.setAdapter(adapter);

        //set up component
        title = v.findViewById(R.id.et_item_title);
        amount = v.findViewById(R.id.et_item_amount);
        quantity = v.findViewById(R.id.et_item_quantity);
        addBtn = v.findViewById(R.id.add_item_btn);
        addBtn.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        activity.setToolBarHeaderText("New item");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        //add item to list and display in recycler view

        ItemModel item = new ItemModel();
        String item_title = title.getText().toString().trim();
        String item_quantity = quantity.getText().toString().trim();
        String item_amount = amount.getText().toString().trim();

        //check title
        try{
            if (item_title.isEmpty()) throw new Exception("Empty title, please try again");
            Double.parseDouble(item_title);
            Toast.makeText(getContext(), "Invalid title, please try again", Toast.LENGTH_LONG).show();
            /*error*/
            return;
        }catch(NumberFormatException e){
            //check for duplication title
            if (!TransactionViewModel.tempTransact.isDuplicateTitle(item_title)){
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
            if (amount < 1 || quantity < 1) throw new Exception("Amount or quantity must be greater than 0, please try again");
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
        ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Confirm adding ?");
        dialog.setNoticeDialogListener(dialog1 -> {
            //insert into list
            Toast.makeText(requireContext(), "Adding successfully", Toast.LENGTH_LONG).show();
            TransactionViewModel.tempTransact.addItem(item);
            adapter.update(TransactionViewModel.tempTransact.getItems());
            dialog1.dismiss();
        });
        dialog.show(getParentFragmentManager(), TAG);
    }

    public void resetEditText(){
        title.setText("");
        amount.setText("");
        quantity.setText("");
    }
}