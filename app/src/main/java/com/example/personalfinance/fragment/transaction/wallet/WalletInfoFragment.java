package com.example.personalfinance.fragment.transaction.wallet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;

import io.reactivex.rxjava3.core.Completable;

public class WalletInfoFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    private WalletViewModel viewModel;
    private EditText wallet_title_et, wallet_amount_et, wallet_description_et;
    private TextView wallet_amount_currency;
    private Button deleteBtn;

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
        viewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);
    }

    private void init(@NonNull View v){
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.configToolbarToReturn(view -> {
            WalletViewModel.walletAction = null;
            getParentFragmentManager().popBackStack();
        });
        activity.setToolBarHeaderText( "Wallet information");

        //set up component
        wallet_title_et = v.findViewById(R.id.wallet_title_et);
        wallet_amount_et = v.findViewById(R.id.wallet_amount_et);
        wallet_description_et = v.findViewById(R.id.wallet_description_et);
        wallet_amount_currency = v.findViewById(R.id.wallet_amount_currency);
        deleteBtn = v.findViewById(R.id.wallet_delete_btn);

        //set up action
        if (WalletViewModel.walletAction == WalletViewModel.WalletAction.update
            || WalletViewModel.walletAction == WalletViewModel.WalletAction.update_transaction){

            WalletModel wallet = null;
            if (WalletViewModel.walletAction == WalletViewModel.WalletAction.update_transaction)
                 wallet = WalletViewModel.useWallet;
            else
                wallet = viewModel.get(WalletViewModel.position);

            wallet_title_et.setText(wallet.getWallet_title());
            WalletModel finalWallet = wallet;
            viewModel.compositeDisposable.add(
                    viewModel
                            .getCurrency()
                            .subscribe(currency -> {
                                wallet_amount_et.setText(UserRepository.formatNumber(UserRepository.toCurrency(finalWallet.getWallet_amount(), currency), false, currency));
                            })
            );
            wallet_description_et.setText(wallet.getWallet_description());

            setCountTransactConstraint(wallet.getId());

            //set up delete btn
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(this);
        }
        else if (WalletViewModel.walletAction == WalletViewModel.WalletAction.create)
            deleteBtn.setVisibility(View.GONE);

        //set up save btn
        v.findViewById(R.id.wallet_save_btn).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //constraint amount edittext
        MainActivity.setMaxDecimalInEditText(wallet_amount_et, 2);
        viewModel.compositeDisposable.add(
                viewModel
                        .getCurrency()
                        .subscribe(currency -> {
                            wallet_amount_currency.setText(currency.toString());
                            //turn off decimal if currency is vnd
                            if (currency == Currency.vnd) wallet_amount_et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            else wallet_amount_et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        })
        );
    }

    public void setCountTransactConstraint(Integer walletId){
        //not allow user to update the amount or delete the wallet if there are any transaction in this wallet
        viewModel.compositeDisposable.add(
                viewModel
                        .countTransactWithWallet(walletId)
                        .subscribe(count -> {
                            if (count != 0){
                                wallet_amount_et.setEnabled(false);
                                deleteBtn.setEnabled(false);
                            }
                            else {
                                wallet_amount_et.setEnabled(true);
                                deleteBtn.setEnabled(true);
                            }
                        })
        );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String header = null;
        if (id == R.id.wallet_delete_btn){
            choice = Choices.DELETE;
            header = "delete";
        }
        else if (id == R.id.wallet_save_btn){
            choice = Choices.SAVE;
            header = "update";
        }
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance("Confirm " + header + " ?");
        confirmDialogFragment.setNoticeDialogListener(this::onDialogPositiveClick);
        confirmDialogFragment.show(getParentFragmentManager(), TAG);
    }

    private void onDialogPositiveClick(DialogFragment dialog) {
            switch(choice){
                case DELETE:
                    //give user warning if deleting a wallet
                    WalletViewModel.completable = viewModel.remove(viewModel.get(WalletViewModel.position));
                    WalletViewModel.walletAction = WalletViewModel.WalletAction.delete;
                    break;
                case SAVE:
                    WalletModel wallet = new WalletModel();
                    try{
                        String title = wallet_title_et.getText().toString();
                        if (title.isEmpty()) throw new Exception("Can not use empty tilte, please try again");

                        String description = wallet_description_et.getText().toString();
                        Double amount = Double.parseDouble(wallet_amount_et.getText().toString().replace(",", ""));

                        wallet.setWallet_title(title);
                        wallet.setWallet_amount(amount);
                        wallet.setWallet_description(description);
                        wallet.setCurrent_use(true);

                        switch(WalletViewModel.walletAction){
                            case create:
                                //get currency
                                WalletViewModel.completable = viewModel
                                        .getCurrency()
                                        .flatMapCompletable(currency -> {
                                            if ((currency == Currency.vnd && wallet.getWallet_amount() == 0d)
                                                    ||(currency == Currency.usd && wallet.getWallet_amount() == 0.00d))
                                                return Completable.error(new Exception("Amount must greater than 0, please try again"));
                                            return Completable.complete();
                                        })
                                        .andThen(viewModel.add(wallet));
                                break;
                            case update:
                                wallet.setId(viewModel.get(WalletViewModel.position).getId());
                                WalletViewModel.completable = viewModel.update(wallet);
                                break;
                            case update_transaction:
                                wallet.setId(WalletViewModel.useWallet.getId());
                                WalletViewModel.completable = viewModel.update(wallet);
                        }
                    }catch(NumberFormatException e){
                        Toast.makeText(getContext(), "Invalid amount, please try again", Toast.LENGTH_LONG).show();
                        WalletViewModel.completable = Completable.error(e);
                    }catch(Exception e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        WalletViewModel.completable = Completable.error(e);
                    }
            }
            getParentFragmentManager().popBackStack();
    }
}