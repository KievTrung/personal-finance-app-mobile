package com.example.personalfinance.fragment.transaction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.dialogFragment.DateTimePickerDialogFragment;
import com.example.personalfinance.fragment.dialogFragment.SingleChoiceDialogFragment;

public class ActualTransactionFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ActualTransactionFragme";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_actual_transaction, container, false);
        init(v);
        return v;
    }

    private enum Type{
        TRANSACTION,
        BILL;
    }
    private MainActivity activity;
    private Type type;
    private String header;

    private void init(View v){
        activity = (MainActivity)getActivity();
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            type = ((bundle.getString("bundleKey").equals("transaction")) ? Type.TRANSACTION : Type.BILL);
            //set header
            header = (type == Type.TRANSACTION) ? "New transaction" : "New bill";
            //set up tool bar
            activity.configToolbarToReturn(activity, getParentFragmentManager(), header) ;
            //set up bill fragment
            if (type == Type.BILL){
                ((EditText)v.findViewById(R.id.amountEt)).setHint("No items");
                ((TextView)v.findViewById(R.id.category_items_tv)).setText("Items");
            }
        });

        ((Button)v.findViewById(R.id.payLaterBtn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.category_items_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.pick_transaction_date_btn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.payLaterBtn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForPayLaterDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.category_items_btn){
            switch(type){
                case TRANSACTION:
                    activity.replaceFragment(R.id.fragment_container, new CategoryFragment(), getContext(),true, null, null);
                    break;
                case BILL:
                    activity.replaceFragment(R.id.fragment_container, new BillItemFragment(), getContext(), true, null, null);
                    break;
            }
        }
        else if (id == R.id.pick_transaction_date_btn){
            new DateTimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
    }

    private SingleChoiceDialogFragment getSingleChoiceForPayLaterDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String choices[] = {"Yes", "No"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("Pay later ?", choices, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            activity.replaceFragment(R.id.fragment_container, new PayLaterFragment(), getContext(), true, null, null);
        });
        return singleChoiceDialogFragment;
    }

    @Override
    public void onResume() {
        activity.setToolBarHeaderText(activity, header);
        super.onResume();
    }
}