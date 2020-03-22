package com.hackthecrisis.gotmilk.ui.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hackthecrisis.gotmilk.LocationSingleton;
import com.hackthecrisis.gotmilk.model.Filter;
import com.hackthecrisis.gotmilk.model.ItemGroup;
import com.hackthecrisis.gotmilk.model.Shop;
import com.hackthecrisis.gotmilk.network.API;
import com.hackthecrisis.gotmilk.network.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends ViewModel implements Callback<JsonObject> {

    private MutableLiveData<ArrayList<Shop>> shopListMutableLiveData;
    private MutableLiveData<ArrayList<ItemGroup>> itemGroupListMutableLiveData;
    private MutableLiveData<String> error;

    private Service service;
    API api;

    public MapViewModel() {
        service = new Service();
        api = service.getService();
        shopListMutableLiveData = new MutableLiveData<>();
        itemGroupListMutableLiveData = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Shop>> getShopListLiveData() {
        return shopListMutableLiveData;
    }

    public LiveData<String> getShopListLoading() {
        return error;
    }

    public LiveData<ArrayList<ItemGroup>> getItemGroupLiveData() {
        return itemGroupListMutableLiveData;
    }

    public void getShopList() {
        api.getNearbyShopList(
                LocationSingleton.location.getLatitude(),
                LocationSingleton.location.getLongitude(),
                1500).enqueue(this);
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
                error.setValue("");
                shopListMutableLiveData.setValue(shops);
            } else
                error.setValue(response.body().get("error").toString());
        } catch (Exception ex) {
            error.setValue(ex.getMessage());
            Log.i("JSON GET shops", ex.getMessage());
        }
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        error.setValue(t.getMessage());
        Log.i("GET shops", t.getMessage());
    }

    public void getItemGroupList() {
        api.getItemGroupList().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Gson gson = new Gson();
                    ArrayList<ItemGroup> itemGroups = new ArrayList<>();
                    JsonArray jsonArray = response.body().getAsJsonArray("item_groups");

                    if(jsonArray != null) {
                        for(int i = 0; i < jsonArray.size(); i++) {
                            itemGroups.add(gson.fromJson(jsonArray.get(i), ItemGroup.class));
                        }
                        error.setValue("");
                        itemGroupListMutableLiveData.setValue(itemGroups);
                    } else
                        error.setValue(response.body().get("error").toString());
                } catch (Exception ex) {
                    error.setValue(ex.getMessage());
                    Log.i("JSON GET item_groups", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                error.setValue(t.getMessage());
                Log.i("GET item_groups", t.getMessage());
            }
        });
    }

    public void getFilteredShopList(ArrayList<Filter> filters) {
        JSONArray jsonArray = new JSONArray();
        for(Filter filter: filters){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", filter.getType());
                jsonObject.put("value", filter.getValue());
                jsonArray.put(jsonObject);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        api.getNearbyShopListWithFilters(LocationSingleton.location.getLatitude(),
                LocationSingleton.location.getLongitude(),
                1500, jsonArray).enqueue(new Callback<JsonObject>() {
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
                        error.setValue("");
                        shopListMutableLiveData.setValue(shops);
                    } else
                        error.setValue(response.body().get("error").toString());
                } catch (Exception ex) {
                    error.setValue(ex.getMessage());
                    Log.i("JSON GET shops filtered", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                error.setValue(t.getMessage());
                Log.i("GET shops filtered", t.getMessage());
            }
        });
    }
}