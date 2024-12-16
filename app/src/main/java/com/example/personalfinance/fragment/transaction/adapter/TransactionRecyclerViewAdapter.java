package com.example.personalfinance.fragment.transaction.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entity.Transact;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.transaction.model.TransactModel;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.TransactViewHolder>{
    private static final String TAG = "kiev";
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private List<TransactModel> transacts;
    private Currency currency;

    public TransactionRecyclerViewAdapter(List<TransactModel> transacts){
        this.transacts = transacts;
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @NonNull
    @Override
    public TransactionRecyclerViewAdapter.TransactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_transaction, parent, false);
        return new TransactViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TransactionRecyclerViewAdapter.TransactViewHolder holder, int position) {
        TransactModel tran = transacts.get(position);
        holder.title.setText(tran.getTran_title() + " " + ((tran.getType() == Transact.Type.bill) ? "(bill)" : ""));
        holder.amount.setText(UserRepository.formatNumber(UserRepository.toCurrency(transacts.get(position).getTran_amount(), currency), true, currency));
        holder.date.setText(tran.getDate_time().format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")));
        holder.category.setText(tran.getCategoryModel().getName());
        holder.itemView.setOnClickListener(v -> {
            itemOnClickListener.onClickListener(position);
        });
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(position);
            return true;
        });
        switch(tran.getCategoryModel().getCategoryType()){
            case spending:
                holder.imageView.setImageResource(R.drawable.money_spend);
                holder.cardView.setBackgroundColor(Color.parseColor("#D25C5C"));
                break;
            case earning:
                holder.imageView.setImageResource(R.drawable.money_earn);
                holder.cardView.setBackgroundColor(Color.parseColor("#41CD19"));
        }
    }

    @Override
    public int getItemCount() {
        return transacts.size();
    }

    public static class TransactViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title, amount, date, category;
        CardView cardView;

        public TransactViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            title = itemView.findViewById(R.id.tran_title);
            amount = itemView.findViewById(R.id.tran_amount);
            date = itemView.findViewById(R.id.tran_date);
            category = itemView.findViewById(R.id.tran_category);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(int position);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(int position);
    }

    public void update(List<TransactModel> newData){
        transacts.clear();
        transacts.addAll(newData);
        notifyDataSetChanged();
    }
}
