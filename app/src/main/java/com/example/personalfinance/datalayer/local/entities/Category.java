package com.example.personalfinance.datalayer.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Category {
    @PrimaryKey
    @NonNull
    private String category_id;
    private String image;
    @NonNull
    private Boolean user_defind;

    public Category(){}

    @NonNull
    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(@NonNull String category_id) {
        this.category_id = category_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @NonNull
    public Boolean getUser_defind() {
        return user_defind;
    }

    public void setUser_defind(@NonNull Boolean user_defind) {
        this.user_defind = user_defind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(category_id, category.category_id) && Objects.equals(image, category.image) && Objects.equals(user_defind, category.user_defind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category_id, image, user_defind);
    }

    @Override
    public String toString() {
        return "Category{" +
                "category_id='" + category_id + '\'' +
                ", image='" + image + '\'' +
                ", user_defind=" + user_defind +
                '}';
    }
}
