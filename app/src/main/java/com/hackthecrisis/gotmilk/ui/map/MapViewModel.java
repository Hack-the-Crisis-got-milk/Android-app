package com.hackthecrisis.gotmilk.ui.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hackthecrisis.gotmilk.model.Shop;
import com.hackthecrisis.gotmilk.network.API;
import com.hackthecrisis.gotmilk.network.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends ViewModel implements Callback<JsonObject> {

    private MutableLiveData<ArrayList<Shop>> shopListMutableLiveData;
    private MutableLiveData<Boolean> shopListLoading;

    private Service service;
    API api;

    public MapViewModel() {
        service = new Service();
        api = service.getService();
        shopListMutableLiveData = new MutableLiveData<>();
        shopListLoading = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Shop>> getShopListLiveData() {
        return shopListMutableLiveData;
    }

    public LiveData<Boolean> getShopListLoading() {
        return shopListLoading;
    }

    public void getShopList() {
        api.getShopList().enqueue(this);
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        try {
            Gson gson = new Gson();
            ArrayList<Shop> shops = new ArrayList<>();
            JsonArray jsonArray = response.body().getAsJsonArray("shops");

            if(jsonArray != null) {
                for(int i = 0; i < jsonArray.size(); i++) {
                    shops.add(gson.fromJson(jsonArray.get(i), Shop.class));
                }
            }
            shopListLoading.setValue(true);
            shopListMutableLiveData.setValue(shops);
        } catch (Exception ex) {
            shopListLoading.setValue(false);
            Log.i("JSON GET shops", ex.getMessage());
        }

    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        shopListLoading.setValue(false);
        Log.i("GET shops", t.getMessage());
    }
}