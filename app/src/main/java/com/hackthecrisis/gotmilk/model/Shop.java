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

    public Shop() {}

    public Shop(String id, String name, ShopLocation location, String address, boolean open_now, String photo) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.address = address;
        this.open_now = open_now;
        this.photo = photo;
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

    @Override
    public String toString() {
        return "Shop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", address='" + address + '\'' +
                ", open_now=" + open_now +
                ", photo='" + photo + '\'' +
                '}';
    }
}
