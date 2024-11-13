package com.example.personalfinance.fragment.transaction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.dialogFragment.SingleChoiceDialogFragment;
import com.example.personalfinance.fragment.transaction.models.WalletModel;
import com.example.personalfinance.fragment.transaction.viewModels.WalletFragmentViewModel;

import io.reactivex.rxjava3.core.Completable;

public class WalletInfoFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    private WalletFragmentViewModel viewModel;
    private MainActivity activity;
    private EditText wallet_title_et, wallet_amount_et, wallet_description_et;


    private enum Choices{ DELETE, SAVE}
    private Choices choice;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet_info, container, false);
        init(v);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view model
        viewModel = new ViewModelProvider(requireActivity()).get(WalletFragmentViewModel.class);
    }

    private void init(@NonNull View v){
        activity = (MainActivity)getActivity();
        activity.configToolbarToReturn(activity, getParentFragmentManager(), "Wallet information");

        //set up component
        wallet_title_et = v.findViewById(R.id.wallet_title_et);
        wallet_amount_et = v.findViewById(R.id.wallet_amount_et);
        wallet_description_et = v.findViewById(R.id.wallet_description_et);

        //set up action
        if (WalletFragmentViewModel.walletAction == WalletFragmentViewModel.WalletAction.update){

            WalletModel wallet = viewModel.get(WalletFragmentViewModel.position);
            wallet_title_et.setText(wallet.getWallet_title());
            wallet_amount_et.setText(String.valueOf(wallet.getWallet_amount()));
            wallet_description_et.setText(wallet.getWallet_description());

            //set up delete btn
            v.findViewById(R.id.wallet_delete_btn).setVisibility(View.VISIBLE);
            v.findViewById(R.id.wallet_delete_btn).setOnClickListener(this);
        }
        else if (WalletFragmentViewModel.walletAction == WalletFragmentViewModel.WalletAction.create){
            v.findViewById(R.id.wallet_delete_btn).setVisibility(View.GONE);
        }

        //set up save btn
        v.findViewById(R.id.wallet_save_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.wallet_delete_btn){
            choice = Choices.DELETE;
        }
        else if (id == R.id.wallet_save_btn){
            choice = Choices.SAVE;
        }
        SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceDialogFragment();
        singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
    }

    private SingleChoiceDialogFragment getSingleChoiceDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Yes", "No"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("Confirm " + choice + " ?", choices, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            switch(choice){
                case DELETE:
                    //todo: check constraint
                    WalletFragmentViewModel.completable = viewModel.remove(WalletFragmentViewModel.position);
                    WalletFragmentViewModel.walletAction = WalletFragmentViewModel.WalletAction.delete;
                    break;
                case SAVE:
                    WalletModel wallet = new WalletModel();
                    //todo: check constraint
                    wallet.setWallet_title(wallet_title_et.getText().toString());
                    wallet.setWallet_amount(Double.parseDouble(wallet_amount_et.getText().toString()));
                    wallet.setWallet_description(wallet_description_et.getText().toString());
                    wallet.setCurrent_use(true);

                    switch(WalletFragmentViewModel.walletAction){
                        case create:
                            WalletFragmentViewModel.completable = viewModel.add(wallet);
                            break;
                        case update:
                            WalletFragmentViewModel.completable = viewModel.update(WalletFragmentViewModel.position, wallet);
                    }
            }
            WalletFragmentViewModel.actionState = WalletFragmentViewModel.ActionState.complete;
            getParentFragmentManager().popBackStack();
        });
        return singleChoiceDialogFragment;
    }
}