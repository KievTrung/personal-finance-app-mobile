package com.example.personalfinance.fragment.transaction.transaction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;
import com.example.personalfinance.fragment.category.CategoryModel;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.dialog.SingleChoiceDialogFragment;
import com.example.personalfinance.fragment.transaction.transaction.adapter.TransactionRecyclerViewAdapter;
import com.example.personalfinance.fragment.transaction.transaction.model.Filter;
import com.example.personalfinance.fragment.transaction.transaction.model.TransactModel;
import com.example.personalfinance.fragment.transaction.wallet.WalletFragment;
import com.example.personalfinance.fragment.transaction.wallet.WalletInfoFragment;
import com.example.personalfinance.fragment.transaction.wallet.WalletViewModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev";
    private MainActivity activity;
    private View v;

    private enum Chart{LINE, PIE }
    private enum ViewType { CHART, LIST, BOTH }
    private Chart chart = Chart.LINE;
    private ViewType viewType;

    private WalletViewModel walletViewModel;
    private TransactionViewModel transactionViewModel;

    private Button walletBtn, chartBtn;

    private TransactionRecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
            Log.d(TAG, "onCreate: " + transactionViewModel.getTransacts().get(position));
            TransactionViewModel.tempTransact = transactionViewModel.getTransacts().get(position);
            TransactionViewModel.action = TransactionViewModel.Action.update;

            if (TransactionViewModel.tempTransact.getType() == Transact.Type.bill){
                transactionViewModel.compositeDisposable.add(
                    transactionViewModel
                            .getAllItem(TransactionViewModel.tempTransact.getTran_id())
                            .subscribe(itemModels -> {
                                TransactionViewModel.tempTransact.setItems(itemModels);
                                activity.replaceFragment(new NewTransactionFragment(), true, null);
                            })
                );
            }
            else{
                activity.replaceFragment(new NewTransactionFragment(), true, null);
            }
        });
        adapter.setItemOnLongClickListener(position -> {
            TransactModel deleteTransact = transactionViewModel.getTransacts().get(position);

            //delete the transaction
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to delete this transaction ?");
            dialog.setNoticeDialogListener(dialog1 -> {
                transactionViewModel.compositeDisposable.add(
                        transactionViewModel
                                .deleteTransact(deleteTransact)
                                .doOnComplete(() -> Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_LONG).show())
                                .andThen(transactionViewModel.fetchAll(deleteTransact.getWallet_id(), transactionViewModel.filter))
                                .subscribe(transactModels -> {
                                    adapter.update(transactModels);
                                    fetchUseWallet();
                                    setUpChart(transactModels, chart);
                                })
                );
            });
            dialog.show(getParentFragmentManager(), TAG);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(View v) throws Exception {
        activity = (MainActivity)getActivity();


        //set up view
        viewType = ViewType.BOTH;

        //set up tool bar
        activity.configToolBarTopRightBtn(View.VISIBLE, R.drawable.wallet, view -> activity.replaceFragment(new WalletFragment(), true, null));
        activity.setToolBarMenuBtnVisibility(View.VISIBLE);

        //set up button
        walletBtn = v.findViewById(R.id.wallet_detail_btn);
        walletBtn.setOnClickListener(this);

        fetchUseWallet();

        //check if there is a request to add category to filter
        if (TransactionViewModel.requestAddCategoryToFilter){
            //retrieve the choosen category
            getParentFragmentManager().setFragmentResultListener("category", this, (requestKey, result) -> {
                CategoryModel categoryModel = (CategoryModel) result.getSerializable("payload");
                try{
                    transactionViewModel.filter.addCategory(categoryModel);
                }catch(RuntimeException e){
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                TransactionViewModel.requestAddCategoryToFilter = false;
                useFilter();
            });
        }
        else
            fetchTransact(transactionViewModel.filter);

        v.findViewById(R.id.create_transaction_btn).setOnClickListener(this);
        chartBtn = v.findViewById(R.id.chart_btn);
        chartBtn.setOnClickListener(this);
        v.findViewById(R.id.view_btn).setOnClickListener(this);
        v.findViewById(R.id.filter_btn).setOnClickListener(this);

        setFromTo();

        //set up recycler view
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view_transaction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //set currency before assign adapter
        transactionViewModel.compositeDisposable.add(
                transactionViewModel
                        .getCurrency().subscribe(currency -> {
                            adapter.setCurrency(currency);
                            recyclerView.setAdapter(adapter);
        }));

        // update the wallet
        if (WalletViewModel.walletAction != null){
            switch(WalletViewModel.walletAction){
                case update_transaction:
                    walletViewModel.compositeDisposable.add(
                            WalletViewModel.completable.andThen(walletViewModel.getUseWallet()).subscribe(walletModel -> {
                                        Log.d(TAG, "onComplete: updating wallet");
                                        Toast.makeText(requireContext(), "Update successfully", Toast.LENGTH_LONG).show();
                                        WalletViewModel.walletAction = null;
                                        setTextForWalletBtn(walletModel.getWallet_amount());
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

    private void setTextForWalletBtn(Double walletAmount) {
        transactionViewModel.compositeDisposable.add(
                transactionViewModel
                        .getCurrency()
                        .subscribe(currency -> {
                            walletBtn.setText(UserRepository.formatNumber(UserRepository.toCurrency(walletAmount, currency), true, currency));
                        })
        );
    }

    public void setUpChart(List<TransactModel> trans, Chart chart){
        transactionViewModel.compositeDisposable.add(
                transactionViewModel.getCurrency().subscribe(currency -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("payload", new ArrayList<>(trans));
                    bundle.putString("currency", currency.toString());
                    getParentFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.chart_fragment_view, (chart == Chart.LINE) ? ChartLineFragment.class : ChartPieFragment.class, bundle)
                            .commit();
                    this.chart = chart;
                })
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchTransact(Filter filter){
        //fetch all transact associated with wallet
        walletViewModel.compositeDisposable.add(
                walletViewModel
                        .getUseWallet()
                        .flatMap(walletModel -> {
                            //fetch transactions from local
                            return transactionViewModel.fetchAll(walletModel.getId(), filter);
                        })
                        .subscribe(transactModels -> {
                            adapter.update(transactModels);
                            setUpChart(transactModels, chart);
                        })
        );
    }

    public void fetchUseWallet(){
        //fetch wallet that being used
        walletViewModel.compositeDisposable.add(
                walletViewModel
                        .getUseWallet()
                        .subscribe(walletModel -> {
                            setTextForWalletBtn(walletModel.getWallet_amount());
                            activity.setToolBarHeaderText(walletModel.getWallet_title());
                            TransactionViewModel.tempTransact.setWallet_id(walletModel.getId());
                        })
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        else if (id == R.id.chart_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForChartDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.view_btn){
            SingleChoiceDialogFragment singleChoiceDialogFragment = getSingleChoiceForViewDialogFragment();
            singleChoiceDialogFragment.show(getParentFragmentManager(), TAG);
        }
        else if (id == R.id.filter_btn){
            useFilter();
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setFromTo(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy - HH:mm");
        ((TextView)v.findViewById(R.id.title_tv)).setText("From : " + transactionViewModel.filter.from.format(formatter) + " to : " + transactionViewModel.filter.to.format(formatter));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void useFilter(){
        FilterDialogFragment dialog = new FilterDialogFragment(transactionViewModel.filter);
        dialog.setListener((dialog1, filter) -> {
            fetchTransact(filter);
            setFromTo();
        });
        dialog.show(getParentFragmentManager(), null);
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
            //set type of transaction that user want to create
            if (singleChoiceDialogFragment.getChoice().equals(choices[0]))
                TransactionViewModel.tempTransact.setType(Transact.Type.transaction);
            else
                TransactionViewModel.tempTransact.setType(Transact.Type.bill);

            //set action
            TransactionViewModel.action = TransactionViewModel.Action.insert;
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
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) v.findViewById(R.id.chart_fragment_view).getLayoutParams();
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) v.findViewById(R.id.transaction_list).getLayoutParams();

            if (singleChoiceDialogFragment.getChoice().equals(choices[0]) && viewType != ViewType.BOTH){
                params1.weight = 1f;
                params2.weight = 1f;
                chartBtn.setVisibility(View.VISIBLE);
                viewType = ViewType.BOTH;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[1]) && viewType != ViewType.CHART){
                params1.weight = 0f;
                params2.weight = 1f;
                chartBtn.setVisibility(View.VISIBLE);
                viewType = ViewType.CHART;
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[2]) && viewType != ViewType.LIST){
                params1.weight = 1f;
                params2.weight = 0f;
                chartBtn.setVisibility(View.GONE);
                viewType = ViewType.LIST;
            }
            v.findViewById(R.id.chart_fragment_view).setLayoutParams(params1);
            v.findViewById(R.id.transaction_list).setLayoutParams(params2);

        });
        return singleChoiceDialogFragment;
    }

    private SingleChoiceDialogFragment getSingleChoiceForChartDialogFragment() {
        SingleChoiceDialogFragment singleChoiceDialogFragment;
        String[] choices = {"Line", "Pie"};
        try {
            singleChoiceDialogFragment = new SingleChoiceDialogFragment("Chart type", choices, (chart == Chart.LINE) ? 0 : 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        singleChoiceDialogFragment.setPositiveListener((dialog, i) -> {
            if (singleChoiceDialogFragment.getChoice().equals(choices[0]) && chart != Chart.LINE){
                setUpChart(transactionViewModel.getTransacts(), Chart.LINE);
                chartBtn.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.line_chart), null, null);
                chartBtn.setText(Chart.LINE.toString());
            }
            else if (singleChoiceDialogFragment.getChoice().equals(choices[1]) && chart != Chart.PIE){
                setUpChart(transactionViewModel.getTransacts(), Chart.PIE);
                chartBtn.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.pie_chart), null, null);
                chartBtn.setText(Chart.PIE.toString());
            }
        });
        return singleChoiceDialogFragment;
    }

    @Override
    public void onStop() {
        activity.getTopRightBtnReference().setVisibility(View.INVISIBLE);
        super.onStop();
    }

}