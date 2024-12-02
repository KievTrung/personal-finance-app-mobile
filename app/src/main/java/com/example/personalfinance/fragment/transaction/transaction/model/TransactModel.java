package com.example.personalfinance.fragment.transaction.transaction.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.Period;
import com.example.personalfinance.fragment.category.CategoryModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactModel implements Parcelable {
    private Integer tran_id;
    private Integer wallet_id;
    private String tran_title;
    private Transact.Type type;
    private CategoryModel categoryModel;
    private LocalDateTime date_time;
    private List<ItemModel> items;
    private Double tran_amount;
    private Period auto_tran;
    private String tran_description;

    public TransactModel(){
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected TransactModel(Parcel in){
        tran_id = in.readInt();
        wallet_id = in.readInt();
        tran_amount = in.readDouble();
        date_time = LocalDateTime.parse(in.readString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getTran_description() {
        return tran_description;
    }

    public void setTran_description(String tran_description) {
        this.tran_description = tran_description;
    }

    public void removeItem(int position){
        if (items != null && position > -1 && position < items.size()){
            items.remove(position);
        }
        else
            Log.d("kiev", "remove error: " + position);
    }

    public void replaceItem(int position, ItemModel item){
        if (items != null){
            ItemModel oldItem = items.get(position);
            item.setId(oldItem.getId());
            item.setBill_id(oldItem.getBill_id());
            items.set(position, item);
        }
    }

    public void addItem(ItemModel item){
        if (items != null){
            items.add(item);
        }
    }

    public double totalItemPrice(){
        if (items == null) return 0d;
        double sum = 0d;
        for(ItemModel item : items)
            sum += (item.getItem_price() * item.getQuantity());
        return sum;
    }

    public boolean isDuplicateTitle(String title, Integer position){
        for(int i=0; i<items.size(); i++){
            if (position != null && position == i)
                continue;
            if (title.equals(items.get(i).getItem_name()))
                return true;
        }
        return false;
    }

    public Transact.Type getType() {
        return type;
    }

    public void setType(Transact.Type type) {
        this.type = type;
    }

    public Period getAuto_tran() {
        return auto_tran;
    }

    public void setAuto_tran(Period auto_tran) {
        this.auto_tran = auto_tran;
    }

    public Double getTran_amount() {
        return tran_amount;
    }

    public void setTran_amount(Double tran_amount) {
        this.tran_amount = tran_amount;
    }

    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setDate_time(LocalDateTime date_time) {
        this.date_time = date_time;
    }

    public String getTran_title() {
        return tran_title;
    }

    public void setTran_title(String tran_title) {
        this.tran_title = tran_title;
    }

    public Integer getTran_id() {
        return tran_id;
    }

    public void setTran_id(Integer tran_id) {
        this.tran_id = tran_id;
    }

    public Integer getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(Integer wallet_id) {
        this.wallet_id = wallet_id;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "TransactModel{" +
                "tran_id=" + tran_id +
                ", wallet_id=" + wallet_id +
                ", tran_title='" + tran_title + '\'' +
                ", categoryModel=" + categoryModel +
                ", date_time=" + date_time +
                ", items=" + items +
                ", tran_amount=" + tran_amount +
                ", auto_tran=" + auto_tran +
                ", tran_description='" + tran_description + '\'' +
                '}';
    }

    public static final Creator<TransactModel> CREATOR = new Creator<TransactModel>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public TransactModel createFromParcel(Parcel source) {
            return new TransactModel(source);
        }

        @Override
        public TransactModel[] newArray(int size) {
            return new TransactModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(tran_id);
        dest.writeInt(wallet_id);
        dest.writeDouble(tran_amount);
        dest.writeString(date_time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
