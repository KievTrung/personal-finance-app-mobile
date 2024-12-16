package com.example.personalfinance.fragment.budget.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.fragment.budget.BudgetTransactionDialogFragment;
import com.example.personalfinance.fragment.budget.model.BudgetModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class BudgetReyclerViewAdapter extends RecyclerView.Adapter<BudgetReyclerViewAdapter.BudgetViewHolder>{
    private static final String TAG = "kiev";
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private List<BudgetModel> budgets = new ArrayList<>();
    private final FragmentManager fragmentManager;

    public BudgetReyclerViewAdapter(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public BudgetReyclerViewAdapter.BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull BudgetReyclerViewAdapter.BudgetViewHolder holder, int position) {
        BudgetModel budget = budgets.get(position);
        //set button
        holder.button.setOnClickListener(v -> {
            BudgetTransactionDialogFragment dialog = new BudgetTransactionDialogFragment(budget);
            dialog.show(fragmentManager, null);
        });
        //set title
        holder.title.setText(budget.getBudget_title());
        //set image
        //set progress bar
        switch(budget.getType()){
            case spending:
                holder.imageView.setImageResource(R.drawable.money_spend);
                holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                break;
            case earning:
                holder.imageView.setImageResource(R.drawable.money_earn);
                holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        //set progress
        holder.progressBar.setProgress(getPercentBudget(budget.getBudget_amount(), budget.getTotalTransactsAmount()).intValue());
        //set listener
        holder.itemView.setOnClickListener(v -> {
            itemOnClickListener.onClickListener(position);
        });
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(position);
            return true;
        });
    }

    private Double getPercentBudget(Double budgetAmount, Double totalTransactAmount){
        if (totalTransactAmount == null) return 0d;
        BigDecimal percent = BigDecimal.valueOf(totalTransactAmount).divide(BigDecimal.valueOf(budgetAmount), RoundingMode.HALF_UP);
        return percent.multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        ProgressBar progressBar;
        ImageButton button;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.name2);
            imageView = itemView.findViewById(R.id.image2);
            progressBar = itemView.findViewById(R.id.progressBar);
            button = itemView.findViewById(R.id.button);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(int position);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(int position);
    }

    public void update(List<BudgetModel> newData){
        budgets.clear();
        budgets.addAll(newData);
        Log.d(TAG, "update: " + budgets);
        notifyDataSetChanged();
    }
}
