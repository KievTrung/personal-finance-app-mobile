package com.example.personalfinance.fragment.wallet;

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
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.transaction.TransactionFragment;
import com.example.personalfinance.fragment.wallet.adapter.WalletRecyclerViewAdapter;
import com.example.personalfinance.fragment.wallet.viewmodel.WalletViewModel;

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
        //set currency before assign adapter
        viewModel.compositeDisposable.add(
                viewModel.getCurrency().subscribe(currency -> {
                    adapter.setCurrency(currency);
                    recyclerView.setAdapter(adapter);
                }));

        //fetch data from local
        viewModel.compositeDisposable.add(
                viewModel.fetchWallets().subscribe((walletModels) -> {
                    adapter.update(walletModels);
                    //set tool bar
                    if (viewModel.isWalletUse() && getParentFragmentManager().getBackStackEntryCount() != 0)
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
                                        Toast.makeText(requireContext(), MessageCode.success_creation, Toast.LENGTH_LONG).show();
                                        viewModel.fetchWallets().subscribe((walletModels) -> chooseWallet());
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: adding wallet, " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), MessageCode.fail_creation, Toast.LENGTH_SHORT).show();
                                        // display error msg
                                        if (throwable.getLocalizedMessage().contains("UNIQUE"))
                                            Toast.makeText(requireContext(), MessageCode.field_title_duplicated, Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(requireContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
                    break;
                case update:
                    viewModel.compositeDisposable.add(
                            WalletViewModel.completable.subscribe(() -> {
                                        Toast.makeText(requireContext(), MessageCode.success_updation, Toast.LENGTH_LONG).show();
                                        viewModel.fetchWallets().subscribe((walletModels) -> adapter.update(walletModels));
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: updating wallet, " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), MessageCode.fail_updation, Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
                case delete:
                    viewModel.compositeDisposable.add(
                            WalletViewModel.completable.subscribe(() -> {
                                        Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_LONG).show();
                                        //fetch data from local
                                        viewModel.fetchWallets().subscribe((walletModels) -> {
                                            adapter.update(walletModels);
                                            //if user delete current use wallet the reconfig the tool bar
                                            boolean use = viewModel.isWalletUse();
                                            activity.setToolBarMenuBtnVisibility(use ? View.INVISIBLE : View.VISIBLE);
                                            activity.setToolBarReturnBtnVisibility(use ? View.VISIBLE : View.INVISIBLE);
                                        });
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: deleting wallet, " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), MessageCode.fail_deletion, Toast.LENGTH_LONG).show();
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