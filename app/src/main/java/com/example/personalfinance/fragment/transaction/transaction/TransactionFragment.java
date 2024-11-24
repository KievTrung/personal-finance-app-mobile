package com.example.personalfinance.fragment.transaction.transaction;

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
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.dialog.DateTimePickerDialogFragment;
import com.example.personalfinance.fragment.dialog.SingleChoiceDialogFragment;
import com.example.personalfinance.fragment.transaction.ChartBarFragment;
import com.example.personalfinance.fragment.transaction.ChartPieFragment;
import com.example.personalfinance.fragment.transaction.transaction.adapter.TransactionRecyclerViewAdapter;
import com.example.personalfinance.fragment.transaction.transaction.model.TransactModel;
import com.example.personalfinance.fragment.transaction.wallet.WalletFragment;
import com.example.personalfinance.fragment.transaction.wallet.WalletInfoFragment;
import com.example.personalfinance.fragment.transaction.wallet.WalletViewModel;

public class TransactionFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    private MainActivity activity;
    private View v;

    private enum Chart{ BAR, PIE }
    private enum ViewType { CHART, LIST, BOTH }
    private Chart chart;
    private ViewType viewType;

    private WalletViewModel walletViewModel;
    private TransactionViewModel transactionViewModel;

    private Button walletBtn;

    private RecyclerView recyclerView;
    private TransactionRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Transact onCreate");

        //init view model
        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        //init adapter
        adapter = new TransactionRecyclerViewAdapter(transactionViewModel.getTransacts());
        adapter.setItemOnClickListener(position -> {
            //update the transaction
            TransactionViewModel.tempTransact = transactionViewModel.getTransacts().get(position);

        });
        adapter.setItemOnLongClickListener(position -> {
            TransactModel deleteTransact = transactionViewModel.getTransacts().get(position);

            //delete the transaction
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to delete this transaction ?");
            dialog.setNoticeDialogListener(dialog1 -> {
                transactionViewModel.compositeDisposable.add(
                        transactionViewModel
                                .deleteTransact(deleteTransact)
                                .doOnComplete(() -> {
                                    Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_LONG).show();
                                })
                                .andThen(transactionViewModel.getAll(deleteTransact.getWallet_id()))
                                .subscribe(transactModels -> {
                                    adapter.update(transactModels);
                                    fetchUseWallet();
                                    dialog1.dismiss();
                                })
                );
            });
            dialog.show(getParentFragmentManager(), TAG);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Transact onCreateView");
        v = inflater.inflate(R.layout.fragment_transaction, container, false);
        try {
            init(v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return v;
    }

    private void init(View v) throws Exception {
        activity = (MainActivity)getActivity();

        //set up view
        viewType = ViewType.BOTH;

        //set up bar chart
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.chart_fragment_view, ChartBarFragment.class, null)
                .commit();
        chart = Chart.BAR;

        //set up tool bar
        activity.configToolBarTopRightBtn(View.VISIBLE, R.drawable.wallet, view -> activity.replaceFragment(new WalletFragment(), true, null));
        activity.setToolBarMenuBtnVisibility(View.VISIBLE);

        //set up button
        walletBtn = v.findViewById(R.id.wallet_detail_btn);
        walletBtn.setOnClickListener(this);

        fetchUseWallet();
        //fetch all transact associated with wallet
        walletViewModel.compositeDisposable.add(
                walletViewModel
                        .getUseWallet()
                        .flatMap(walletModel -> {
                            //fetch transactions from local
                            return transactionViewModel.getAll(walletModel.getId());
                        })
                        .subscribe(transactModels -> adapter.update(transactModels))
        );


        v.findViewById(R.id.create_transaction_btn).setOnClickListener(this);
        v.findViewById(R.id.transaction_begin_date_btn).setOnClickListener(this);
        v.findViewById(R.id.transaction_end_date_btn).setOnClickListener(this);
        v.findViewById(R.id.chart_btn).setOnClickListener(this);
        v.findViewById(R.id.view_btn).setOnClickListener(this);

        //set up recycler view
        recyclerView = v.findViewById(R.id.recycler_view_transaction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // update the wallet
        if (WalletViewModel.walletAction != null){
            switch(WalletViewModel.walletAction){
                case update_transaction:
                    walletViewModel.compositeDisposable.add(
                            WalletViewModel.completable.andThen(walletViewModel.getUseWallet()).subscribe(walletModel -> {
                                        Log.d(TAG, "onComplete: updating wallet");
                                        Toast.makeText(requireContext(), "Update successfully", Toast.LENGTH_LONG).show();
                                        WalletViewModel.walletAction = null;
                                        walletBtn.setText(String.valueOf(walletModel.getWallet_amount()));
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: updating wallet");
                                        Log.d(TAG, "onError: " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), "Updating failed", Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
                case delete:
                    walletViewModel.compositeDisposable.add(
                            WalletViewModel.completable.subscribe(() -> {
                                        Log.d(TAG, "onComplete: deleting wallet");
                                        Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_LONG).show();
                                        WalletViewModel.walletAction = null;
                                        activity.replaceFragment(new WalletFragment(), false, null);
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: deleting wallet");
                                        Log.d(TAG, "onError: " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
            }
        }
    }

    public void fetchUseWallet(){
        //fetch wallet that being used
        walletViewModel.compositeDisposable.add(
                walletViewModel
                        .getUseWallet()
                        .subscribe(walletModel -> {
                            walletBtn.setText(String.valueOf(walletModel.getWallet_amount()));
                            activity.setToolBarHeaderText(walletModel.getWallet_title());
                            TransactionViewModel.tempTransact.setWallet_id(walletModel.getId());
                        })
        );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.wallet_detail_btn){
            //this is a piece of shit
            WalletViewModel.walletAction = WalletViewModel.WalletAction.update_transaction;
            activity.replaceFragment(new WalletInfoFragment(), true, null);
        }
        else if (id == R.id.create_transaction_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForNewTransactionDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.transaction_begin_date_btn){
            new DateTimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.transaction_end_date_btn){
            new DateTimePickerDialogFragment().show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.chart_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForChartDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.view_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForViewDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
    }

    //new transaction
    private SingleChoiceDialogFragment getSingleChoiceForNewTransactionDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Transaction", "Bill"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("Transaction type", choices, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            //set type of transaction that you want to create
            if (singleChoiceDialogFragment.getChoice().equals(choices[0]))
                TransactionViewModel.tempTransact.setType(Transact.Type.transaction);
            else
                TransactionViewModel.tempTransact.setType(Transact.Type.bill);

            //receive resutl info of new transaction
            getParentFragmentManager().setFragmentResultListener("transact", this, (requestKey, result1) -> {

                //reload recycler view if create success
                String state = result1.getString("payload");
                if (state.equals("success"))
                    transactionViewModel.compositeDisposable.add(
                            walletViewModel
                                    .getUseWallet()
                                    .flatMap(walletModel -> transactionViewModel.getAll(walletModel.getId()))
                                    .subscribe(transactModels -> adapter.update(transactModels))
                    );
            });
            activity.replaceFragment(new NewTransactionFragment(), true, null);
        });
        return singleChoiceDialogFragment;
    }

    private SingleChoiceDialogFragment getSingleChoiceForViewDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Both", "Chart", "List"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("View type", choices, (viewType == ViewType.BOTH) ? 0 : ((viewType == ViewType.CHART) ? 1 : 2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            int chartView = 0, listView = 0;
            if (singleChoiceDialogFragment.getChoice().equals(choices[0]) && viewType != ViewType.BOTH){
                chartView = View.VISIBLE;
                listView = View.VISIBLE;
                viewType = ViewType.BOTH;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[1]) && viewType != ViewType.CHART){
                chartView = View.VISIBLE;
                listView = View.GONE;
                viewType = ViewType.CHART;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[2]) && viewType != ViewType.LIST){
                chartView = View.GONE;
                listView = View.VISIBLE;
                viewType = ViewType.LIST;
            }

            if (listView == View.GONE) squeezeInView(v.findViewById(R.id.transaction_list));
            else if (listView == View.VISIBLE) squeezeOutView(v.findViewById(R.id.transaction_list));

            if (chartView == View.GONE) squeezeInView(v.findViewById(R.id.chart_fragment_view));
            else if (chartView == View.VISIBLE) squeezeOutView(v.findViewById(R.id.chart_fragment_view));
        });
        return singleChoiceDialogFragment;
    }

    private SingleChoiceDialogFragment getSingleChoiceForChartDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Bar", "Pie"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("Chart type", choices, (chart == Chart.BAR) ? 0 : 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            if (singleChoiceDialogFragment.getChoice().equals(choices[0]) && chart != Chart.BAR){
                activity.replaceFragment(new ChartBarFragment(),false,null);
                chart = Chart.BAR;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[1]) && chart != Chart.PIE){
                activity.replaceFragment(new ChartPieFragment(), false, null);
                chart = Chart.PIE;
            }
        });
        return singleChoiceDialogFragment;
    }

    public void squeezeInView(final View view) {
        view.animate()
                .scaleY(0f)
                .setDuration(500)
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    public void squeezeOutView(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setScaleY(0f);
        view.setPivotY(view.getHeight());
        view.animate()
                .scaleY(1f)
                .setDuration(500)
                .start();
    }


    @Override
    public void onStop() {
        activity.getTopRightBtnReference(activity).setVisibility(View.INVISIBLE);
        super.onStop();
    }

}