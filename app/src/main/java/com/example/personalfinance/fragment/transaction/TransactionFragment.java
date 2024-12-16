package com.example.personalfinance.fragment.transaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
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
import com.example.personalfinance.datalayer.local.entity.Transact;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.category.model.CategoryModel;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.dialog.SingleChoiceDialogFragment;
import com.example.personalfinance.fragment.transaction.adapter.TransactionRecyclerViewAdapter;
import com.example.personalfinance.fragment.transaction.model.Filter;
import com.example.personalfinance.fragment.transaction.model.TransactModel;
import com.example.personalfinance.fragment.transaction.viewmodel.TransactionViewModel;
import com.example.personalfinance.fragment.wallet.WalletFragment;
import com.example.personalfinance.fragment.wallet.WalletInfoFragment;
import com.example.personalfinance.fragment.wallet.viewmodel.WalletViewModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
                                .doOnComplete(() -> Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_LONG).show())
                                .andThen(transactionViewModel.fetchAll(deleteTransact.getWallet_id(), transactionViewModel.filter))
                                .subscribe(transactModels -> {
                                    adapter.update(transactModels);
                                    fetchUseWallet();
                                    setUpChart(transactModels, chart);
                                },throwable -> {
                                    Toast.makeText(requireContext(), MessageCode.fail_deletion, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
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

        v.findViewById(R.id.create_transaction_btn).setOnClickListener(this);
        chartBtn = v.findViewById(R.id.chart_btn);
        chartBtn.setOnClickListener(this);
        if (chart == Chart.LINE){
            chartBtn.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.line_chart), null, null);
            chartBtn.setText(Chart.LINE.toString());
        }
        else {
            chartBtn.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.pie_chart), null, null);
            chartBtn.setText(Chart.PIE.toString());
        }

        v.findViewById(R.id.view_btn).setOnClickListener(this);
        v.findViewById(R.id.filter_btn).setOnClickListener(this);

        setFromTo();

        v.findViewById(R.id.fab_print).setOnClickListener(this);

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
                                        Toast.makeText(requireContext(), MessageCode.success_updation, Toast.LENGTH_LONG).show();
                                        WalletViewModel.walletAction = null;
                                        setTextForWalletBtn(walletModel.getWallet_amount());
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: updating wallet");
                                        Log.d(TAG, "onError: " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), MessageCode.fail_updation, Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
                case delete:
                    walletViewModel.compositeDisposable.add(
                            WalletViewModel.completable.subscribe(() -> {
                                        Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_LONG).show();
                                        WalletViewModel.walletAction = null;
                                        activity.replaceFragment(new WalletFragment(), false, null);
                                    }
                                    , throwable -> {
                                        Log.d(TAG, "onError: deleting wallet");
                                        Log.d(TAG, "onError: " + throwable.getLocalizedMessage());
                                        Toast.makeText(requireContext(), MessageCode.fail_deletion, Toast.LENGTH_LONG).show();
                                    })
                    );
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
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
            });
            TransactionViewModel.requestAddCategoryToFilter = false;
            useFilter();
        }
        else
            fetchTransact(transactionViewModel.filter);
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

                            /*set wallet id*/
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
        else if (id == R.id.fab_print){
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to generate report for this wallet ?");
            dialog.setNoticeDialogListener(this::createPdfDocument);
            dialog.show(getParentFragmentManager(), TAG);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPdfDocument(DialogFragment dialogFragment) {

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(550, 1000, 1).create();
        float center = pageInfo.getPageWidth()/2-20;
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(true);

        //create a document
        PdfDocument pdfDocument = new PdfDocument();
        //start a new page
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        //put thing on page
        Canvas canvas = page.getCanvas();
        paint.setTextSize(15f);
        paint.setColor(Color.BLUE);
        //set header
        canvas.drawText("Financial Report", center, 25, paint);

        //wallet part
        paint.setTextSize(10f);
        paint.setColor(Color.GRAY);
        canvas.drawText("Wallet information", 10, 50, paint);

        paint.setColor(Color.BLACK);

        transactionViewModel.compositeDisposable.add(
                transactionViewModel.getCurrency().subscribe(currency -> {
                    walletViewModel.compositeDisposable.add(
                            walletViewModel.getUseWallet().subscribe(walletModel -> {
                                String[] wallet_field = { "Title", "Amount", "From", "To", "Description" };
                                int startX = 10;
                                int endX = pageInfo.getPageWidth() - 10;
                                int startY = 70;
                                int offset = 70;
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy - HH:mm");
                                for (String s : wallet_field) {
                                    paint.setStyle(Paint.Style.FILL);
                                    canvas.drawText(s, startX, startY, paint);
                                    if (s.equals(wallet_field[0]))
                                        canvas.drawText(walletModel.getWallet_title(), startX + offset, startY, paint);
                                    else if (s.equals(wallet_field[1]))
                                        canvas.drawText(UserRepository.formatNumber(UserRepository.toCurrency(walletModel.getWallet_amount(), currency), true, currency), startX + offset, startY, paint);
                                    else if (s.equals(wallet_field[2]))
                                        canvas.drawText(transactionViewModel.filter.from.format(formatter), startX + offset, startY, paint);
                                    else if (s.equals(wallet_field[3]))
                                        canvas.drawText(transactionViewModel.filter.to.format(formatter), startX + offset, startY, paint);
                                    else if (s.equals(wallet_field[4])) {
                                        canvas.drawText(walletModel.getWallet_description(), startX + offset, startY, paint);
                                        break;
                                    }

                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setStrokeWidth(0.5f);
                                    canvas.drawLine(startX, startY + 3, endX, startY + 3, paint);

                                    startY += 20;
                                }
                                canvas.drawLine(65, 60, 65, 170, paint);

                                // Draw table header
                                int[] columnWidths = {30, 70, 100, 100, 100, 130};
                                int currentX = 10;
                                int startY__ = 200;
                                int rowHeight = 30;
                                String[] headers = {"No", "Title", "Date", "Amount", "Category", "Description"};
                                for (int i = 0; i < headers.length; i++) {
                                    // Draw rectangle
                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setStrokeWidth(0.5f);
                                    canvas.drawRect(currentX, startY__, currentX + columnWidths[i], startY__ + rowHeight, paint);
                                    // Draw text inside rectangle
                                    paint.setStyle(Paint.Style.FILL);
                                    canvas.drawText(headers[i], currentX + 10, startY__ + 20, paint);
                                    currentX += columnWidths[i];
                                }

                                List<TransactModel> transactModels = transactionViewModel.getTransacts();
                                int currentX_ = 10;
                                int startY_ = 230;
                                int rowHeight_ = 20;
                                float spend = 0, earn = 0;

                                for (int i=0; i<transactModels.size(); i++){
                                    TransactModel tran = transactModels.get(i);

                                    //create entry of graph
                                    if (tran.getCategoryModel().getCategoryType() == CategoryType.spending)
                                        spend += tran.getTran_amount().floatValue();
                                    else
                                        earn += tran.getTran_amount().floatValue();

                                    paint.setStyle(Paint.Style.FILL);
                                    switch(tran.getCategoryModel().getCategoryType()){
                                        case earning:
                                            paint.setColor(Color.GREEN);
                                            break;
                                        case spending:
                                            paint.setColor(Color.RED);
                                    }
                                    canvas.drawRect(currentX_, startY_+5, pageInfo.getPageWidth()-10, startY_ + rowHeight_ + 5, paint);

                                    paint.setStyle(Paint.Style.FILL);
                                    paint.setColor(Color.BLACK);
                                    canvas.drawText(String.valueOf(i), currentX_ + 6, startY_ + 20, paint);
                                    currentX_ += columnWidths[0];//title
                                    canvas.drawText(tran.getTran_title(), currentX_ + 6, startY_ + 20, paint);
                                    currentX_ += columnWidths[1];//date
                                    canvas.drawText(tran.getDate_time().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")), currentX_ + 6, startY_ + 20, paint);
                                    currentX_ += columnWidths[2];//amount
                                    canvas.drawText(UserRepository.formatNumber(UserRepository.toCurrency(tran.getTran_amount(), currency), true, currency), currentX_ + 6, startY_ + 20, paint);
                                    currentX_ += columnWidths[3];//category
                                    canvas.drawText(tran.getCategoryModel().getName(), currentX_ + 6, startY_ + 20, paint);
                                    currentX_ += columnWidths[4];//description
                                    canvas.drawText(tran.getTran_description(), currentX_ + 6, startY_ + 20, paint);
                                    startY_ += 20;
                                    currentX_ = 10;
                                }

                                float sum = spend + earn;
                                paint.setColor(Color.BLACK);
                                canvas.drawText("Summary : " + (spend/sum)*100 + "/" + (earn/sum)*100 + " % (spend/earn)", pageInfo.getPageWidth()-230, startY_ + 30, paint);

                                pdfDocument.finishPage(page);
                                saveFilePdf(pdfDocument, walletModel.getWallet_title() +"_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("d_MM_yyyy_HH_mm_ss")));
                            })
                    );
                })
        );
    }

    private void saveFilePdf(PdfDocument pdfDocument, String title){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Check if external storage is available
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // Save the document to a file in a public directory
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));
            if (!directory.exists()){
                if (directory.mkdirs()) Log.d(TAG, "createPdfDocument: create dir success");
                else Log.d(TAG, "createPdfDocument: create dir failed");
            }
            else
                Log.d(TAG, "createPdfDocument: folder already existed");

            File file = new File(directory, "/"+title);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                pdfDocument.writeTo(fos);
                Toast.makeText(getContext(), MessageCode.success_creation, Toast.LENGTH_LONG).show();

                //send to pdf viewer fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("payload", file);
                getParentFragmentManager().setFragmentResult("file", bundle);

                activity.replaceFragment(new PdfViewerFragment(), true, null);
            } catch (IOException e) {
                Log.d(TAG, "createPdfDocument: " + e.fillInStackTrace());
            } finally {
                pdfDocument.close();
            }
        }
    }

}