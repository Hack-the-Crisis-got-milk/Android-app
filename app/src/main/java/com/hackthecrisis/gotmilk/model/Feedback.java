package com.hackthecrisis.gotmilk.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feedback {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("item_group_id")
    @Expose
    private String item_group_id;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("shop_id")
    @Expose
    private String shop_id;

    public Feedback() {}

    public Feedback(String type, String item_group_id, String value, String shop_id) {
        this.type = type;
        this.item_group_id = item_group_id;
        this.value = value;
        this.shop_id = shop_id;
    }

    public String getType() {
        return type;
    }

    public String getItem_group_id() {
        return item_group_id;
    }

    public String getValue() {
        return value;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setItem_group_id(String item_group_id) {
        this.item_group_id = item_group_id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "type='" + type + '\'' +
                ", item_group_id='" + item_group_id + '\'' +
                ", value='" + value + '\'' +
                ", shop_id='" + shop_id + '\'' +
                '}';
    }
}
