package com.example.personalfinance.fragment.category.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.fragment.category.CategoryModel;

import java.util.List;

public class CategoryEarningRecyclerViewAdapter extends RecyclerView.Adapter<CategoryEarningRecyclerViewAdapter.EarningViewHolder> {
    private CategorySpendingRecyclerViewAdapter.ItemOnClickListener itemOnClickListener;
    private CategorySpendingRecyclerViewAdapter.ItemOnLongClickListener itemOnLongClickListener;
    private List<CategoryModel> categories;

    public CategoryEarningRecyclerViewAdapter(List<CategoryModel> categories) {
        this.categories = categories;
    }

    public void setItemOnClickListener(CategorySpendingRecyclerViewAdapter.ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(CategorySpendingRecyclerViewAdapter.ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public CategoryEarningRecyclerViewAdapter.EarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_view_row_category_earning, parent, false);
        return new CategoryEarningRecyclerViewAdapter.EarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningViewHolder holder, int position) {
        holder.item_name.setText(categories.get(position).getName());
        holder.itemView.setOnClickListener(v -> itemOnClickListener.onClickListener(position));
        holder.imageView.setImageResource(R.drawable.money_earn);
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class EarningViewHolder extends RecyclerView.ViewHolder{
        TextView item_name;
        ImageView imageView;

        public EarningViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.earning_name);
            imageView = itemView.findViewById(R.id.earning_image);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(int position);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(List<CategoryModel> newData){
        categories.clear();
        categories.addAll(newData);
        notifyDataSetChanged();
    }
}
