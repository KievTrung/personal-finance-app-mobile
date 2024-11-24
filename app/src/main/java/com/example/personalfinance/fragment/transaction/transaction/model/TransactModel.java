package com.example.personalfinance.fragment.transaction.transaction.model;

import android.util.Log;

import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.enums.Period;
import com.example.personalfinance.fragment.category.CategoryModel;

import java.time.LocalDateTime;
import java.util.List;

public class TransactModel {
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

    public void addItem(ItemModel item){
        if (items != null){
            items.add(item);
        }
    }

    public Double totalItemPrice(){
        if (items == null) return 0d;
        Double sum = 0d;
        for(ItemModel item : items)
            sum += item.getItem_price() * item.getQuantity();
        return sum;
    }

    public boolean isDuplicateTitle(String title){
        for(ItemModel item : items)
            if (title.equals(item.getItem_name())) return true;
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
}
