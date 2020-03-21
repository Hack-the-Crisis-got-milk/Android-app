package com.hackthecrisis.gotmilk.network;

import com.google.gson.JsonObject;
import com.hackthecrisis.gotmilk.model.ItemGroup;
import com.hackthecrisis.gotmilk.model.Shop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

public interface API {

    @GET("shops/")
    Call<JsonObject> getShopList();

    @GET("shops/nearby/")
    Call<ArrayList<Shop>> getNearbyShopList();

    @GET("itemgroups/")
    Call<ArrayList<ItemGroup>> getItemGroupList();
}
