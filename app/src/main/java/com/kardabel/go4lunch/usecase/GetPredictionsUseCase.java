package com.kardabel.go4lunch.usecase;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.Predictions;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.AutocompleteRepository;
import com.kardabel.go4lunch.repository.LocationRepository;

public class GetPredictionsUseCase {

    private final LocationRepository locationRepository;
    private final AutocompleteRepository autocompleteRepository;

    // RETRIEVE AUTOCOMPLETE PREDICTIONS RESULTS FROM LOCATION
    public GetPredictionsUseCase(
            LocationRepository locationRepository,
            AutocompleteRepository autocompleteRepository) {

        this.locationRepository = locationRepository;
        this.autocompleteRepository = autocompleteRepository;

    }




    public LiveData<Predictions> invoke(String text) {
        return Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<Predictions>>() {
            @Override
            public LiveData<Predictions> apply(Location input) {
                String locationAsText = input.getLatitude() + "," + input.getLongitude();
                return Transformations.map(autocompleteRepository.getAutocompleteResultListLiveData(locationAsText, text), new Function<Predictions, Predictions>() {
                    @Override
                    public Predictions apply(Predictions input) {
                        return input;
                    }
                });

            }
        });
    }
}
