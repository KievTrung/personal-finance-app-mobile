package com.example.personalfinance.fragment.setting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.setting.adapter.CurrencyRecyclerViewAdapter;

import io.reactivex.rxjava3.disposables.Disposable;

public class CurrencyDialogFragment extends DialogFragment {
    public interface NoticeDialogListener{
        void onDialogPositiveClick(DialogFragment dialog, Currency currency);
    }
    private final UserRepository userRepository;
    private Currency currency;
    private NoticeDialogListener listener;

    private RecyclerView recyclerView;
    private CurrencyRecyclerViewAdapter adapter;

    public CurrencyDialogFragment(){
        userRepository = new UserRepository(getContext());
    }

    public void setNoticeDialogListener(NoticeDialogListener noticeDialogListener){
        this.listener = noticeDialogListener;
    }

    private Disposable disposable;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_currency, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setTitle("Currency preference")
                .setPositiveButton("Confirm", (dialog, id) -> listener.onDialogPositiveClick(this, currency))
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        recyclerView = v.findViewById(R.id.recycler_view_currency);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        disposable = userRepository.getCurrency().subscribe(currency -> {
            this.currency = currency;
            adapter = new CurrencyRecyclerViewAdapter(currency);
            adapter.setItemOnClickListener(currency_ -> {
                this.currency = currency_;
            });
            adapter.setItemOnLongClickListener(currency_ -> {/*do nothing*/});
            recyclerView.setAdapter(adapter);
        });
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        disposable.dispose();
        AppLocalDatabase.closeDb();
        super.onDismiss(dialog);
    }
}
