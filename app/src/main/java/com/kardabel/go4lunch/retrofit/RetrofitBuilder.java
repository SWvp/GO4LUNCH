package com.kardabel.go4lunch.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private GoogleMapsApi googleMapsApi;
    private static RetrofitBuilder retrofitBuilderInstance;

    private RetrofitBuilder(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // INSTANCE FOR INTERFACE
        googleMapsApi = retrofit.create(GoogleMapsApi.class);
    }

    public static RetrofitBuilder getInstance(){
        if (retrofitBuilderInstance == null) {
            retrofitBuilderInstance = new RetrofitBuilder();
        }
        return retrofitBuilderInstance;
    }

    public GoogleMapsApi getGoogleMapsApiFromRetrofitBuilder(){ return googleMapsApi; }
}
