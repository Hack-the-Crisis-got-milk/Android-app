package com.hackthecrisis.gotmilk.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://ec2-18-130-190-158.eu-west-2.compute.amazonaws.com:8010/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private Retrofit retrofit2 = new Retrofit.Builder()
            .baseUrl("https://secret-mountain-57394.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public API getService() {
        return retrofit.create(API.class);
    }

    public API getService2() {
        return retrofit2.create(API.class);
    }
}
