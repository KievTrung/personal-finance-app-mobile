package com.example.personalfinance.fragment.entry;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.MainActivityViewModel;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entity.User;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.CurrencyDialogFragment;
import com.example.personalfinance.fragment.wallet.WalletFragment;

public class EntryFragment extends Fragment {
    private static final String TAG = "kiev";
    private MainActivity activity;
    private MainActivityViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init viewModel
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        viewModel.compositeDisposable.add(
                viewModel.isUserExisted().subscribe(userCount -> {
                    if (userCount == 0){
                        //set default value for user
                        Log.d(TAG, "onCreate: invoked 1" );
                        User user = new User(Currency.usd, false);
                        viewModel.compositeDisposable.add(viewModel.setUser(user).subscribe(() -> Log.d(TAG, "onCreate: set user ")));
                    }
                    else{
                        Log.d(TAG, "onCreate: invoked 2");
                        activity.replaceFragment(new WalletFragment(), false, null);
                    }
                })
        );
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_entry, container, false);

        //get main activity
        activity = (MainActivity)getActivity();

        v.findViewById(R.id.proceed_btn).setOnClickListener(v1 -> {
            //ask user for the preference currency
            CurrencyDialogFragment dialog = new CurrencyDialogFragment();
            dialog.setNoticeDialogListener((dialog1, currency) -> {
                viewModel.compositeDisposable.add(
                        viewModel
                                .switchCurrency(currency)
                                .subscribe(() -> {
                                    Toast.makeText(requireContext(), "switch to " + currency, Toast.LENGTH_LONG).show();
                                    activity.replaceFragment(new WalletFragment(), false, null);
                                }
                                ,throwable -> Log.d(TAG, "error: " + throwable.getMessage()))
                );
            });
            dialog.show(getParentFragmentManager(), null);
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set tool bar
        activity.setToolBarReturnBtnVisibility(View.INVISIBLE);
        activity.setToolBarHeaderText("Welcome user");
        activity.setToolBarMenuBtnVisibility(View.INVISIBLE);
    }
}