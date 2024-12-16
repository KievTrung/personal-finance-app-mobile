package com.example.personalfinance.fragment.category.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.fragment.category.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.SpendingViewHolder> {
    private CategoryRecyclerViewAdapter.ItemOnClickListener itemOnClickListener;
    private CategoryRecyclerViewAdapter.ItemOnLongClickListener itemOnLongClickListener;
    private List<CategoryModel> categories;

    public CategoryRecyclerViewAdapter() {
        this.categories = new ArrayList<>();
    }

    public void setItemOnClickListener(CategoryRecyclerViewAdapter.ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(CategoryRecyclerViewAdapter.ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public CategoryRecyclerViewAdapter.SpendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_category, parent, false);
        return new CategoryRecyclerViewAdapter.SpendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingViewHolder holder, int position) {
        holder.item_name.setText(categories.get(position).getName());
        switch (categories.get(position).getCategoryType()){
            case spending:
                holder.imageView.setImageResource(R.drawable.money_spend);
                break;
            case earning:
                holder.imageView.setImageResource(R.drawable.money_earn);
        }
        holder.itemView.setOnClickListener(v -> itemOnClickListener.onClickListener(position));
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class SpendingViewHolder extends RecyclerView.ViewHolder{
        TextView item_name;
        ImageView imageView;

        public SpendingViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(int position);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(int position);
    }

    public void update(List<CategoryModel> newData){
        categories.clear();
        categories.addAll(newData);
        notifyDataSetChanged();
    }
}
