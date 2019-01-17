package com.nifcloud.mbaas.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Notification {
    @SerializedName("categories")
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "categories=" + categories +
                '}';
    }
}
