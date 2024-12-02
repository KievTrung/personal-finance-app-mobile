package com.example.personalfinance.fragment.transaction.transaction.adapter;

import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.repositories.UserRepository;
import com.example.personalfinance.fragment.transaction.transaction.model.ItemModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder>{
    private static final String TAG = "kiev";
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private List<ItemModel> items = new ArrayList<>();
    private Currency currency;

    public ItemRecyclerViewAdapter(){}

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
    public ItemRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerViewAdapter.ItemViewHolder holder, int position) {
        holder.quantity.setText(String.valueOf(items.get(position).getQuantity()));
        holder.price.setText(UserRepository.formatNumber(UserRepository.toCurrency(items.get(position).getItem_price(), currency), true, currency));
        holder.title.setText(items.get(position).getItem_name());
        holder.itemView.setOnClickListener(v -> itemOnClickListener.onClickListener(position));
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView title, quantity, price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.card_item_quantity);
            title = itemView.findViewById(R.id.card_item_title);
            price = itemView.findViewById(R.id.card_item_price);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(int position);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(int position);
    }

    public void update(List<ItemModel> newData){
        items.clear();
        items.addAll(newData);
        notifyDataSetChanged();
    }
}
