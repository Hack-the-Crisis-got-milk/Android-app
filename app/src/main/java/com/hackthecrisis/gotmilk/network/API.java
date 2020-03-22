package com.hackthecrisis.gotmilk.network;

import com.google.gson.JsonObject;
import com.hackthecrisis.gotmilk.model.Filter;
import com.hackthecrisis.gotmilk.model.ItemGroup;
import com.hackthecrisis.gotmilk.model.Shop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    @GET("shops/")
    Call<JsonObject> getShopList();

    @GET("shops/nearby/")
    Call<JsonObject> getNearbyShopList(@Query("lat") double lat,
                                            @Query("lng") double lng,
                                            @Query("radius") int radius);

    @GET("shops/nearby/")
    Call<JsonObject> getNearbyShopListWithFilters(@Query("lat") double lat,
                                                  @Query("lng") double lng,
                                                  @Query("radius") int radius,
                                                  @Query("filters")JSONArray filters);


    @GET("itemgroups/")
    Call<JsonObject> getItemGroupList();
}
