package com.example.personalfinance.fragment.transaction.wallet;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.transaction.transaction.TransactionFragment;

public class WalletFragment extends Fragment{
    private static final String TAG = "kiev";
    private WalletViewModel viewModel;
    private RecyclerView recyclerView;
    private WalletRecyclerViewAdapter adapter;
    private MainActivity activity;


    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: wallet fragment");

        //init view model
        viewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);

        //init recycler adapter
        adapter = new WalletRecyclerViewAdapter(viewModel.getWallets());
        adapter.setItemOnClickListener(position -> {
            if (!viewModel.get(position).getCurrent_use())
                viewModel.useWallet(position).subscribe(() -> chooseWallet());
            else
                chooseWallet();

        });
        adapter.setItemOnLongClickListener(position -> {
            //communicate with WalletInfo to update Wallet
            WalletViewModel.walletAction = WalletViewModel.WalletAction.update;
            WalletViewModel.position = position;
            ((MainActivity)getActivity()).replaceFragment(new WalletInfoFragment(), true, null);
        });

        //get host activity
        setActivity((MainActivity)getActivity());
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        init(v);
        return v;
    }

    @SuppressLint("CheckResult")
    private void init(View v){
        //set up create wallet button
        v.findViewById(R.id.create_wallet_btn).setOnClickListener((view)->{
            // communicate with WalletInfo to create Wallet
            WalletViewModel.walletAction = WalletViewModel.WalletAction.create;
            activity.replaceFragment(new WalletInfoFragment(),true, null);
        });

        //init recycler view
        recyclerView = v.findViewById(R.id.wallet_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //fetch data from local
        viewModel.compositeDisposable.add(
                viewModel.fetchWallets().subscribe((walletModels) -> {
                    adapter.update(walletModels);
                    //set tool bar
                    if (viewModel.isWalletUse())
                        activity.configToolbarToReturn(view -> getParentFragmentManager().popBackStack());
                    else{
                        activity.setToolBarMenuBtnVisibility(View.VISIBLE);
                        activity.setToolBarReturnBtnVisibility(View.INVISIBLE);
                    }
                })
        );

        activity.setToolBarHeaderText("My Wallet");

        //receive confirm response from wallet info
        if (WalletViewModel.walletAction != null){
            switch(WalletViewModel.walletAction){
                case create:
                    viewModel.compositeDisposable.add(
                            WalletViewModel.completable.subscribe(() -> {
                                        Log.d(TAG, "onComplete: adding wallet");
                                        Toast.makeText(requireContext(), "Creating successfully", Toast.LENGTH_LONG).show();
                                        viewModel.fetchWallets().subscribe((walletModels) -> chooseWallet());
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: adding wallet");
                                        Log.d(TAG, "onError: " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), "Creating failed", Toast.LENGTH_SHORT).show();
                                        // display error msg
                                        if (throwable.getLocalizedMessage().contains("UNIQUE"))
                                            Toast.makeText(requireContext(), "Wallet title existed, please try again", Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
                case update:
                    viewModel.compositeDisposable.add(
                            WalletViewModel.completable.subscribe(() -> {
                                        Log.d(TAG, "onComplete: updating wallet");
                                        Toast.makeText(requireContext(), "Update successfully", Toast.LENGTH_LONG).show();
                                        viewModel.fetchWallets().subscribe((walletModels) -> adapter.update(walletModels));
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: updating wallet");
                                        Log.d(TAG, "onError: " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), "Updating failed", Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
                case delete:
                    viewModel.compositeDisposable.add(
                            WalletViewModel.completable.subscribe(() -> {
                                        Log.d(TAG, "onComplete: deleting wallet");
                                        Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_LONG).show();
                                        //fetch data from local
                                        viewModel.fetchWallets().subscribe((walletModels) -> {
                                            adapter.update(walletModels);
                                            //if use delete current use wallet the reconfig the tool bar
                                            boolean use = viewModel.isWalletUse();
                                            activity.setToolBarMenuBtnVisibility(use ? View.INVISIBLE : View.VISIBLE);
                                            activity.setToolBarReturnBtnVisibility(use ? View.VISIBLE : View.INVISIBLE);
                                        });
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: deleting wallet");
                                        Log.d(TAG, "onError: " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
            }
            WalletViewModel.walletAction = null;
        }
    }

    public void chooseWallet(){
        //return result back to caller fragment
        if (getParentFragmentManager().getBackStackEntryCount() != 0)
            getParentFragmentManager().popBackStack();
        else
            activity.replaceFragment(new TransactionFragment(),false, null);
    }

}