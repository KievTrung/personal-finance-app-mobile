package com.example.personalfinance.fragment.transaction;

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
import com.example.personalfinance.fragment.transaction.models.WalletModel;
import com.example.personalfinance.fragment.transaction.recyclerViewAdapters.WalletRecyclerViewAdapter;
import com.example.personalfinance.fragment.transaction.viewModels.WalletFragmentViewModel;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class WalletFragment extends Fragment{
    private static final String TAG = "kiev";
    private WalletFragmentViewModel viewModel;
    private RecyclerView recyclerView;
    private WalletRecyclerViewAdapter adapter;
    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        init(v);
        return v;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init view model
        viewModel = new ViewModelProvider(requireActivity()).get(WalletFragmentViewModel.class);

        //init recycler adapter
        adapter = new WalletRecyclerViewAdapter(viewModel.getWallets());
        adapter.setItemOnClickListener(position -> {
            if (!viewModel.get(position).getCurrent_use())
                viewModel.useWallet(position).subscribe(() -> {
                    adapter.notifyDataSetChanged();
                    //todo: navigate back to transaction fragment
                }
                , throwable -> {});
        });
        adapter.setItemOnLongClickListener(position -> {
            //communicate with WalletInfo to update Wallet
            WalletFragmentViewModel.walletAction = WalletFragmentViewModel.WalletAction.update;
            WalletFragmentViewModel.position = position;
            ((MainActivity)getActivity()).replaceFragment(R.id.fragment_container, new WalletInfoFragment(), getContext(), true, null, null);
        });

        //get host activity
        setActivity((MainActivity)getActivity());
        activity.configToolbarToReturn(activity, getParentFragmentManager(), "My Wallet");

    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    private void init(View v){
        //set up create wallet button
        v.findViewById(R.id.create_wallet_btn).setOnClickListener((view)->{
            // communicate with WalletInfo to create Wallet
            WalletFragmentViewModel.walletAction = WalletFragmentViewModel.WalletAction.create;
            ((MainActivity)getActivity()).replaceFragment(R.id.fragment_container, new WalletInfoFragment(), getContext(), true, null, null);
        });

        //init recycler view
        recyclerView = v.findViewById(R.id.wallet_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //fetch data from local
        viewModel.fetchWallets().subscribe((walletModels) -> adapter.update(walletModels));

        //receive confirm response from wallet info
        if (WalletFragmentViewModel.walletAction != null && WalletFragmentViewModel.actionState != null){
            switch(WalletFragmentViewModel.walletAction){
                case create:
                    WalletFragmentViewModel.completable.subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.d(TAG, "onSubscribe: adding wallet");
//                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete: adding wallet");
                            Toast.makeText(requireContext(), "Creating successfully", Toast.LENGTH_LONG).show();
                            viewModel.fetchWallets().subscribe((walletModels) -> adapter.update(walletModels));
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, "onError: adding wallet");
                            Log.d(TAG, "onError: " + e.getLocalizedMessage());
                            Toast.makeText(requireContext(), "Creating failed", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case update:
                    WalletFragmentViewModel.completable.subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.d(TAG, "onSubscribe: updating wallet");
//                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete: updating wallet");
                            Toast.makeText(requireContext(), "Update successfully", Toast.LENGTH_LONG).show();
                            viewModel.fetchWallets().subscribe((walletModels) -> adapter.update(walletModels));
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, "onError: updating wallet");
                            Log.d(TAG, "onError: " + e.getLocalizedMessage());
                            Toast.makeText(requireContext(), "Updating failed", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case delete:
                    WalletFragmentViewModel.completable.subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.d(TAG, "onSubscribe: deleting wallet");
//                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete: deleting wallet");
                            Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_LONG).show();
                            //fetch data from local
                            viewModel.fetchWallets().subscribe((walletModels) -> adapter.update(walletModels));
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, "onError: deleting wallet");
                            Log.d(TAG, "onError: " + e.getLocalizedMessage());
                            Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
            }
            WalletFragmentViewModel.walletAction = null;
            WalletFragmentViewModel.actionState = null;

        }
    }

}