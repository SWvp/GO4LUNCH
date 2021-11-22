package com.kardabel.go4lunch.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.AutocompleteResponse;
import com.kardabel.go4lunch.pojo.Prediction;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutocompleteRepository {

    private final GoogleMapsApi googleMapsApi;
    private final String key = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";


    public AutocompleteRepository(GoogleMapsApi googleMapsApi){
        this.googleMapsApi = googleMapsApi;

    }


    public LiveData<AutocompleteResponse> getAutocompleteResultListLiveData(String location,
                                                                            String input){




        String type = "establishment";
        String radius = "1000";

        MutableLiveData<AutocompleteResponse> AutocompleteResultMutableLiveData = new MutableLiveData<>();

            googleMapsApi.autocompleteResult(key, type, location, radius, input).enqueue(
                    new Callback<AutocompleteResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<AutocompleteResponse> call, @NonNull Response<AutocompleteResponse> response) {
                            if (response.body() != null) {
                                AutocompleteResultMutableLiveData.setValue(response.body());

                            }
                        }

                        @Override
                        public void onFailure(Call<AutocompleteResponse> call, Throwable t) {
                            t.printStackTrace();

                        }
                    });

        return AutocompleteResultMutableLiveData;

    }


 // public void autocomplete(String location,
 //                          String input){

 //     String type = "establishment";
 //     String radius = "1000";
 //      List<Prediction> list= new ArrayList<>();

 //     MutableLiveData<AutocompleteResponse> AutocompleteResultMutableLiveData = new MutableLiveData<>();

 //     googleMapsApi.autocompleteResult(key, type, location, radius, input).enqueue(
 //             new Callback<AutocompleteResponse>() {
 //                 @Override
 //                 public void onResponse(@NonNull Call<AutocompleteResponse> call, @NonNull Response<AutocompleteResponse> response) {
 //                     if (response.body() != null) {

 //                         list.addAll(response.body().getPredictions());
 //                         AutocompleteResultMutableLiveData.setValue(response.body());

 //                     }
 //                 }

 //                 @Override
 //                 public void onFailure(Call<AutocompleteResponse> call, Throwable t) {
 //                     t.printStackTrace();

 //                 }
 //             });

 // }

 // public LiveData<AutocompleteResponse> getAUto() throws IOException {

 //     OkHttpClient client = new OkHttpClient().newBuilder()
 //             .build();
 //     Request request = new Request.Builder()
 //             .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=amoeba&types=establishment&location=37.76999%2C-122.44696&radius=500&key=YOUR_API_KEY")
 //             .method("GET", null)
 //             .build();
 //     okhttp3.Response response = client.newCall(request).execute();




 // }
}
