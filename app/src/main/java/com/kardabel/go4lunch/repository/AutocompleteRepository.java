package com.kardabel.go4lunch.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.BuildConfig;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.pojo.Predictions;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutocompleteRepository {

    private final GoogleMapsApi googleMapsApi;
    private final Application application;

    public AutocompleteRepository(
            GoogleMapsApi googleMapsApi,
            Application application) {
        this.googleMapsApi = googleMapsApi;
        this.application = application;

    }

    public LiveData<Predictions> getAutocompleteResultListLiveData(String location,
                                                                   String input) {

        String key = BuildConfig.GOOGLE_PLACES_KEY;
        String type = application.getString(R.string.autocomplete_type);
        String radius = application.getString(R.string.radius);

        MutableLiveData<Predictions> AutocompleteResultMutableLiveData = new MutableLiveData<>();

        googleMapsApi.autocompleteResult(key, type, location, radius, input).enqueue(
                new Callback<Predictions>() {
                    @Override
                    public void onResponse(@NonNull Call<Predictions> call, @NonNull Response<Predictions> response) {
                        if (response.body() != null) {
                            AutocompleteResultMutableLiveData.setValue(response.body());

                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Predictions> call, @NonNull Throwable t) {
                        t.printStackTrace();

                    }
                });
        return AutocompleteResultMutableLiveData;

    }
}
