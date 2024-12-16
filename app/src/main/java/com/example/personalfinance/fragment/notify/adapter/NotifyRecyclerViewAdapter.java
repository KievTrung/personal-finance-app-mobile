package com.example.personalfinance.fragment.notify.adapter;

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

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entity.Notify;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotifyRecyclerViewAdapter extends RecyclerView.Adapter<NotifyRecyclerViewAdapter.NotifyViewHolder>{
    private static final String TAG = "kiev";
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private List<Notify> notifies = new ArrayList<>();

    public NotifyRecyclerViewAdapter(){}

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_notify, parent, false);
        return new NotifyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
        Notify notify = notifies.get(position);
        holder.header.setText(notify.getHeader());
        holder.content.setText(notify.getContent());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy - HH:mm");
        holder.date.setText(notify.getDateTime().format(formatter));
        holder.imageView.setImageResource(MainActivity.setNotifyIcon(notify.getType()));
        holder.cardView.setBackgroundColor(Color.parseColor((notify.getRead()) ? "#C2BFBF" : "#FFFFFF"));
        //set listener
        holder.itemView.setOnClickListener(v -> {
            itemOnClickListener.onClickListener(position);
        });
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notifies.size();
    }

    public static class NotifyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView header, content, date;
        CardView cardView;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.notify_image);
            header = itemView.findViewById(R.id.notify_header);
            content = itemView.findViewById(R.id.notify_content);
            date = itemView.findViewById(R.id.notify_date);
            cardView = itemView.findViewById(R.id.card_view_notify);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(int position);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(int position);
    }

    public void update(List<Notify> newData){
        notifies.clear();
        notifies.addAll(newData);
        notifyDataSetChanged();
    }
}
