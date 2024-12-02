package com.example.personalfinance.fragment.account;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.enums.Language;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;


public class AccountFragment extends Fragment{
    private static final String TAG = "kiev";
    private Button btn;
    private AccountViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init viewModel
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_account, container, false);
        init(v);
        return v;
    }

    public void init(View v){
        MainActivity activity = (MainActivity)getActivity();
        activity.setToolBarReturnBtnVisibility(View.INVISIBLE);
        activity.setToolBarHeaderText("");

        btn = v.findViewById(R.id.create_account_btn);

        User user = new User();
        user.setUserId(1);
        user.setUserName("trung");
        user.setPassword("hello");
        user.setLanguage(Language.vn);
        user.setEmail("helo");
        user.setSyncState(SyncState.synced);
        user.setCurrency(Currency.vnd);

        btn.setOnClickListener(v1 -> {
            viewModel.compositeDisposable.add(
                    viewModel
                            .setUser(user)
                            .subscribe(() -> Toast.makeText(requireContext(), "set user success", Toast.LENGTH_LONG).show()
                                    ,throwable -> Log.d(TAG, "error: " + throwable.getMessage()))
            );
        });
        v.findViewById(R.id.switch_currency).setOnClickListener(v1 -> {
            viewModel.compositeDisposable.add(
                    viewModel
                            .switchCurrency(Currency.usd)
                            .subscribe(() -> Toast.makeText(requireContext(), "switch success", Toast.LENGTH_LONG).show()
                                    ,throwable -> Log.d(TAG, "error: " + throwable.getMessage()))
            );
        });
        v.findViewById(R.id.switch_currency_vnd).setOnClickListener(v1 -> {
            viewModel.compositeDisposable.add(
                    viewModel
                            .switchCurrency(Currency.vnd)
                            .subscribe(() -> Toast.makeText(requireContext(), "switch success", Toast.LENGTH_LONG).show()
                                    ,throwable -> Log.d(TAG, "error: " + throwable.getMessage()))
            );
        });
    }

}