package com.hackthecrisis.gotmilk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemGroup {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("shop_type")
    @Expose
    private String shop_type;

    public ItemGroup() {}

    public ItemGroup(String id, String name, String icon, String shop_type) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.shop_type = shop_type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getShop_type() {
        return shop_type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setShop_type(String shop_type) {
        this.shop_type = shop_type;
    }

    @Override
    public String toString() {
        return "ItemGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", shop_type='" + shop_type + '\'' +
                '}';
    }
}
