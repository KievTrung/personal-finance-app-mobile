package com.example.personalfinance.fragment.category.model;

import com.example.personalfinance.datalayer.local.enums.CategoryType;

import java.io.Serializable;
import java.util.Objects;

public class CategoryModel implements Serializable {
    private Integer id;
    private String name;
    private CategoryType categoryType;

    public CategoryModel(){}

    public CategoryModel(String name, CategoryType categoryType){
        this.name = name;
        this.categoryType = categoryType;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CategoryModel that = (CategoryModel) object;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && categoryType == that.categoryType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, categoryType);
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryType=" + categoryType +
                '}';
    }
}
