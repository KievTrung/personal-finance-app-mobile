package com.example.personalfinance.fragment.setting.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinance.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportRecyclerViewAdapter extends RecyclerView.Adapter<ReportRecyclerViewAdapter.ReportViewHolder>{
    private static final String TAG = "kiev";
    private ItemOnClickListener itemOnClickListener;
    private ItemOnLongClickListener itemOnLongClickListener;
    private List<File> files = new ArrayList<>();

    public ReportRecyclerViewAdapter(){}

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_file, parent, false);
        return new ReportViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        File file = files.get(position);
        holder.fileDate.setText(getFileDate(file));
        holder.fileName.setText(file.getName());
        //set listener
        holder.itemView.setOnClickListener(v -> {
            itemOnClickListener.onClickListener(file);
        });
        holder.itemView.setOnLongClickListener(v -> {
            itemOnLongClickListener.onLongClickListener(file);
            return true;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFileDate(File file){
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"));
        } catch (IOException e) {
            Log.d(TAG, "getFileDate: " + e.getLocalizedMessage());
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder{
        TextView fileName, fileDate;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileDate = itemView.findViewById(R.id.file_date);
        }
    }

    public interface ItemOnClickListener {
        void onClickListener(File file);
    }

    public interface ItemOnLongClickListener{
        void onLongClickListener(File file);
    }

    public void update(List<File> newData){
        files.clear();
        files.addAll(newData);
        notifyDataSetChanged();
    }
}
