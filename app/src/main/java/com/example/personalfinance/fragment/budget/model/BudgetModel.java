package com.example.personalfinance.fragment.budget.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.category.model.CategoryModel;
import com.example.personalfinance.fragment.transaction.model.Filter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BudgetModel {
    private Integer id;
    private String budget_title;
    private Double budget_amount;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private CategoryType type;
    private List<CategoryModel> categories = new ArrayList<>();
    private List<CategoryModel> newCategory = new ArrayList<>();
    private List<CategoryModel> deleteCategory = new ArrayList<>();
    private Double totalTransactsAmount;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public BudgetModel(){
        budget_title = "";
        start_date = Filter.setDefaultStartDate();
        end_date = Filter.setDefaultEndDate();
        type = CategoryType.spending;
    }

    public Double getTotalTransactsAmount() {
        return totalTransactsAmount;
    }

    public void setTotalTransactsAmount(Double totalTransactsAmount) {
        this.totalTransactsAmount = totalTransactsAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBudget_title() {
        return budget_title;
    }

    public void setBudget_title(String budget_title) {
        this.budget_title = budget_title;
    }

    public Double getBudget_amount() {
        return budget_amount;
    }

    public void setBudget_amount(Double budget_amount) {
        this.budget_amount = budget_amount;
    }

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDateTime start_date) {
        this.start_date = start_date;
    }

    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDateTime end_date) {
        this.end_date = end_date;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {
        this.categories = categories;
    }

    public void removeCategory(int position){
        categories.remove(position);
    }

    public void addCategory(CategoryModel category){
        if (categories.contains(category))
            throw new RuntimeException(MessageCode.field_title_duplicated);
        if (type != category.getCategoryType())
            throw new RuntimeException(MessageCode.category_not_match_type + ", This category is type " + category.getCategoryType() + ", the use type is " + type);
        categories.add(category);
    }

    @Override
    public String toString() {
        return "BudgetModel{" +
                "id=" + id +
                ", budget_title='" + budget_title + '\'' +
                ", budget_amount=" + budget_amount +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", type=" + type +
                ", categories=" + categories +
                ", totalTransactsAmount=" + totalTransactsAmount +
                '}';
    }
}
