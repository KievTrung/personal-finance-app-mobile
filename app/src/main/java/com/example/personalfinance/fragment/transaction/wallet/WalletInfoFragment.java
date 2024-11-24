package com.example.personalfinance.fragment.transaction.wallet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.dialog.SingleChoiceDialogFragment;

import io.reactivex.rxjava3.core.Completable;

public class WalletInfoFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    private WalletViewModel viewModel;
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
        viewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);
    }

    private void init(@NonNull View v){
        activity = (MainActivity)getActivity();
        activity.configToolbarToReturn(view -> {
            WalletViewModel.walletAction = null;
            getParentFragmentManager().popBackStack();
        });
        activity.setToolBarHeaderText( "Wallet information");

        //set up component
        wallet_title_et = v.findViewById(R.id.wallet_title_et);
        wallet_amount_et = v.findViewById(R.id.wallet_amount_et);
        wallet_description_et = v.findViewById(R.id.wallet_description_et);

        //set up action
        if (WalletViewModel.walletAction == WalletViewModel.WalletAction.update){

            WalletModel wallet = viewModel.get(WalletViewModel.position);
            wallet_title_et.setText(wallet.getWallet_title());
            wallet_amount_et.setText(String.valueOf(wallet.getWallet_amount()));
            wallet_description_et.setText(wallet.getWallet_description());

            setCountTransactConstraint(wallet.getId());

            //set up delete btn
            v.findViewById(R.id.wallet_delete_btn).setVisibility(View.VISIBLE);
            v.findViewById(R.id.wallet_delete_btn).setOnClickListener(this);
        }
        else if (WalletViewModel.walletAction == WalletViewModel.WalletAction.create){
            v.findViewById(R.id.wallet_delete_btn).setVisibility(View.GONE);
        }
        else if (WalletViewModel.walletAction == WalletViewModel.WalletAction.update_transaction){

            WalletModel wallet = WalletViewModel.useWallet;
            wallet_title_et.setText(wallet.getWallet_title());
            wallet_amount_et.setText(String.valueOf(wallet.getWallet_amount()));
            wallet_description_et.setText(wallet.getWallet_description());

            setCountTransactConstraint(wallet.getId());

            //set up delete btn
            v.findViewById(R.id.wallet_delete_btn).setVisibility(View.VISIBLE);
            v.findViewById(R.id.wallet_delete_btn).setOnClickListener(this);
        }


        //set up save btn
        v.findViewById(R.id.wallet_save_btn).setOnClickListener(this);
    }

    public void setCountTransactConstraint(Integer walletId){
        //not allow user to update the amount if there are any transaction in this wallet
        viewModel.compositeDisposable.add(
                viewModel
                        .countTransactWithWallet(walletId)
                        .subscribe(count -> {
                            if (count != 0) wallet_amount_et.setEnabled(false);
                            else wallet_amount_et.setEnabled(true);
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
                    //todo: implement when user delete a wallet
                    ConfirmDialogFragment deleteDialog = ConfirmDialogFragment.newInstance("Delete will wipe out all the transaction associating with the wallet\nDo you confirm ?");
                    deleteDialog.setNoticeDialogListener(dialog1 -> {
                        if (WalletViewModel.walletAction == WalletViewModel.WalletAction.update)
                            WalletViewModel.completable = viewModel.remove(viewModel.get(WalletViewModel.position));
                        else
                            WalletViewModel.completable = viewModel.remove(WalletViewModel.useWallet);
                        WalletViewModel.walletAction = WalletViewModel.WalletAction.delete;
                    });
                    deleteDialog.show(getParentFragmentManager(), TAG);
                    break;
                case SAVE:
                    WalletModel wallet = new WalletModel();
                    try{
                        String title = wallet_title_et.getText().toString();
                        if (title.isEmpty()) throw new Exception("Can not use empty tilte, please try again");

                        String description = wallet_description_et.getText().toString();
                        Double amount = Double.parseDouble(wallet_amount_et.getText().toString());
                        if (amount < 0) throw new Exception("Amount must be positive, please try again");

                        wallet.setWallet_title(title);
                        wallet.setWallet_amount(amount);
                        wallet.setWallet_description(description);
                        wallet.setCurrent_use(true);

                        switch(WalletViewModel.walletAction){
                            case create:
                                WalletViewModel.completable = viewModel.add(wallet);
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