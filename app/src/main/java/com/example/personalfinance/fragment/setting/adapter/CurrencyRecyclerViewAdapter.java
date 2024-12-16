package com.example.personalfinance.fragment.setting.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.Currency;

import java.util.Arrays;
import java.util.List;

public class CurrencyRecyclerViewAdapter extends RecyclerView.Adapter<CurrencyRecyclerViewAdapter.CurrencyViewHolder>{
    private static final String TAG = "kiev";
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private final List<Currency> currencies;
    int lastSelectedPosition;

    public CurrencyRecyclerViewAdapter(Currency currency_pref){
        currencies = Arrays.asList(Currency.values());

        for (int i=0; i<currencies.size(); i++)
            if (currencies.get(i) == currency_pref)
                lastSelectedPosition = i;
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_currency, parent, false);
        return new CurrencyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Currency currency = currencies.get(position);

        holder.radioButton.setText(currency.name());
        holder.radioButton.setChecked(position == lastSelectedPosition);
        holder.radioButton.setOnClickListener(v -> {
            int copyOfLastCheck = lastSelectedPosition;
            lastSelectedPosition = position;
            notifyItemChanged(copyOfLastCheck);
            notifyItemChanged(lastSelectedPosition);
            itemOnClickListener.onClickListener(currency);
        });

        //set listener
        holder.itemView.setOnClickListener(v -> {
        });
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(currency);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    public class CurrencyViewHolder extends RecyclerView.ViewHolder{
        RadioButton radioButton;

        public CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_btn_currency);
        }

    }

    public interface ItemOnClickListener {
        void onClickListener(Currency currency);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(Currency currency);
    }
}
