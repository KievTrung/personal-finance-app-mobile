package com.example.personalfinance.fragment.category.adapter;

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

public class CategorySpendingRecyclerViewAdapter extends RecyclerView.Adapter<CategorySpendingRecyclerViewAdapter.SpendingViewHolder> {
    private CategorySpendingRecyclerViewAdapter.ItemOnClickListener itemOnClickListener;
    private CategorySpendingRecyclerViewAdapter.ItemOnLongClickListener itemOnLongClickListener;
    private List<CategoryModel> categories;

    public CategorySpendingRecyclerViewAdapter(List<CategoryModel> categories) {
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
    public CategorySpendingRecyclerViewAdapter.SpendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_views_row_category_spending, parent, false);
        return new CategorySpendingRecyclerViewAdapter.SpendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingViewHolder holder, int position) {
        holder.item_name.setText(categories.get(position).getName());
        holder.imageView.setImageResource(R.drawable.money_spend);
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
            item_name = itemView.findViewById(R.id.spending_name);
            imageView = itemView.findViewById(R.id.spending_image);
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
