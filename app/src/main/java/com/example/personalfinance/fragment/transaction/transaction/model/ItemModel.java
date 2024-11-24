package com.example.personalfinance.fragment.transaction.transaction.model;


public class ItemModel {
    private Integer id;
    private Integer bill_id;
    private String item_name;
    private Integer quantity;
    private Double item_price;

    public ItemModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBill_id() {
        return bill_id;
    }

    public void setBill_id(Integer bill_id) {
        this.bill_id = bill_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getItem_price() {
        return item_price;
    }

    public void setItem_price(Double item_price) {
        this.item_price = item_price;
    }

    @Override
    public String toString() {
        return "ItemModel{" +
                "id=" + id +
                ", bill_id=" + bill_id +
                ", item_name='" + item_name + '\'' +
                ", quantity=" + quantity +
                ", item_price=" + item_price +
                '}';
    }
}
