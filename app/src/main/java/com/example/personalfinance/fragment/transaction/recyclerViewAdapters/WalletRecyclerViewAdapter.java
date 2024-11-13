package com.example.personalfinance.fragment.transaction.recyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;
import com.example.personalfinance.fragment.transaction.models.WalletModel;

import java.util.List;

public class WalletRecyclerViewAdapter extends RecyclerView.Adapter<WalletRecyclerViewAdapter.WalletViewHolder>{
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private List<WalletModel> wallets;

    public WalletRecyclerViewAdapter(List<WalletModel> wallets){
        this.wallets = wallets;
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public WalletRecyclerViewAdapter.WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_recycler_view_row, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletRecyclerViewAdapter.WalletViewHolder holder, int position) {
        holder.wallet_title.setText(wallets.get(position).getWallet_title());
        holder.wallet_amount.setText(String.valueOf(wallets.get(position).getWallet_amount()));
        holder.wallet_description.setText(wallets.get(position).getWallet_description());
        holder.itemView.setOnClickListener(v -> itemOnClickListener.onClickListener(position));
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(position);
            return true;
        });
        holder.starImageView.setVisibility(wallets.get(position).getCurrent_use() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder{
        ImageView starImageView;
        TextView wallet_title, wallet_amount, wallet_description;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            starImageView = itemView.findViewById(R.id.star_image);
            wallet_title = itemView.findViewById(R.id.wallet_title);
            wallet_amount = itemView.findViewById(R.id.wallet_amount);
            wallet_description = itemView.findViewById(R.id.wallet_description);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(int position);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(int position);
    }

    public void update(List<WalletModel> newData){
        wallets.clear();
        wallets.addAll(newData);
        notifyDataSetChanged();
    }
}
