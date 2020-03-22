package com.hackthecrisis.gotmilk.ui.grocery;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hackthecrisis.gotmilk.LocationSingleton;
import com.hackthecrisis.gotmilk.model.Feedback;
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

public class GroceryViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Shop>> shopListMutableLiveData;
    private MutableLiveData<ArrayList<ItemGroup>> itemGroupListMutableLiveData;
    private MutableLiveData<ArrayList<Feedback>> feedbackMutableLiveData;
    private MutableLiveData<Boolean> feedbackSent;
    private MutableLiveData<String> error;

    private Service service;
    API api;
    API api2;

    public GroceryViewModel() {
        shopListMutableLiveData = new MutableLiveData<>();
        itemGroupListMutableLiveData = new MutableLiveData<>();
        error = new MutableLiveData<>();
        service = new Service();
        api = service.getService();
        api2 = service.getService2();
        feedbackMutableLiveData = new MutableLiveData<>();
        feedbackSent = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Shop>> getShopListLiveData() {
        return shopListMutableLiveData;
    }

    public LiveData<ArrayList<ItemGroup>> getItemGroupLiveData() {
        return itemGroupListMutableLiveData;
    }

    public LiveData<String> getShopListLoading() {
        return error;
    }

    public LiveData<ArrayList<Feedback>> getFeedbackLiveData() {
        return feedbackMutableLiveData;
    }

    public LiveData<Boolean> getFeedbackSent() {
        return feedbackSent;
    }

    public void getShopList() {
        shopListMutableLiveData.setValue(null);
        api.getNearbyShopList(
                LocationSingleton.location.getLatitude(),
                LocationSingleton.location.getLongitude(),
                1500).enqueue(new Callback<JsonObject>() {
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
        });
    }

    public void getItemGroupList() {
        itemGroupListMutableLiveData.setValue(null);
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
        shopListMutableLiveData.setValue(null);
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

    public void getFeedbackForShops(String id, ArrayList<Shop> shops) {
        feedbackMutableLiveData.setValue(null);
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        api2.getFeedbackForShops(jsonArray).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Gson gson = new Gson();
                    ArrayList<Feedback> feedbacks = new ArrayList<>();
                    JsonObject jsonObject = response.body().getAsJsonObject("response");

                    for(Shop shop: shops) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray(shop.getId());
                        if(jsonArray != null) {
                            for(int i = 0; i < jsonArray.size(); i++) {
                                feedbacks.add(gson.fromJson(jsonArray.get(i), Feedback.class));
                            }
                            error.setValue("");
                        } else
                            continue;
                    }
                    feedbackMutableLiveData.setValue(feedbacks);
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

    public void sendFeedback(String item_group_id, String type, String value, String shop_id) {
      //  feedbackSent.setValue(null);
        api2.provideFeedback(type, item_group_id, value, shop_id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i("call", call.request().body().toString());
                if(response.code() == 200)
                    feedbackSent.setValue(true);
                else
                    feedbackSent.setValue(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                feedbackSent.setValue(false);
            }
        });
    }
}