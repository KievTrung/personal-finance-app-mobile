package com.example.personalfinance.fragment.transaction.transaction.adapter;

import com.example.personalfinance.fragment.transaction.transaction.model.ItemModel;

import android.os.Build;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder>{
    private static final String TAG = "kiev";
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private List<ItemModel> items = new ArrayList<>();

    public ItemRecyclerViewAdapter(){}

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public ItemRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerViewAdapter.ItemViewHolder holder, int position) {
        holder.price.setText(String.valueOf(items.get(position).getItem_price()));
        holder.quantity.setText(String.valueOf(items.get(position).getQuantity()));
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
