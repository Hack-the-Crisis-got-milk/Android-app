package com.hackthecrisis.gotmilk.model;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shop {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("loc")
    @Expose
    ShopLocation location;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("open_now")
    @Expose
    private boolean open_now;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("shop_type")
    @Expose
    private String shop_type;

    public Shop() {}

    public Shop(String id, String name, ShopLocation location, String address, boolean open_now, String photo, double distance, String shop_type) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.address = address;
        this.open_now = open_now;
        this.photo = photo;
        this.distance = distance;
        this.shop_type = shop_type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ShopLocation getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public String getPhoto() {
        return photo;
    }

    public double getDistance() {
        return distance;
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

    public void setLocation(ShopLocation location) {
        this.location = location;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setShop_type(String shop_type) {
        this.shop_type = shop_type;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", address='" + address + '\'' +
                ", open_now=" + open_now +
                ", photo='" + photo + '\'' +
                ", distance=" + distance +
                ", shop_type='" + shop_type + '\'' +
                '}';
    }
}
