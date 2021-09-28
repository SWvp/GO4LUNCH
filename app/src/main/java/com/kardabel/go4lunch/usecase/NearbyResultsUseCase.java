package com.kardabel.go4lunch.usecase;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbyResponseRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class NearbyResultsUseCase {

    public LiveData<PlaceSearchResults> placeSearchResultsLiveData;
    public LiveData<PlaceDetailsResult> placeDetailsResultLiveData;
    private MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private LocationRepository locationRepository = new LocationRepository();
    private NearbyResponseRepository nearbyResponseRepository = new NearbyResponseRepository();
    private final String key = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";

    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public NearbyResultsUseCase() {

        placeSearchResultsLiveData = Transformations.switchMap(locationRepository
                .getLocationLiveData(), new Function<Location, LiveData<PlaceSearchResults>>() {
            @Override
            public LiveData<PlaceSearchResults> apply(Location input) {
                locationLiveData.setValue(input);
                String locationAsText = input.getLatitude() + "," + input.getLongitude();
                assert nearbyResponseRepository != null;
                return nearbyResponseRepository.getRestaurantListLiveData(
                        key,
                        "restaurant",
                        locationAsText,
                        "1000");

            }
        });

    }

    // FAIRE NIMP POUR AVOIR DES DETAILS NOM DE DIEU
    public void getDetails(){
        for (int i = 0; i < placeSearchResultsLiveData.getValue().getResults().size(); i++) {
            String place_id = placeSearchResultsLiveData.getValue().getResults().get(i).getPlaceId();
            nearbyResponseRepository.getRestaurantOpeningHours(key, place_id);

        }
    }

    // TRANSLATE CURRENT DAY IN NUMERIC (FROM SUNDAY == 0)
    public int getCurrentNumericDay(){
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();
        int currentNumericDay = -1;
        switch (currentDay){
            case SUNDAY:
                currentNumericDay = 0;
                break;
            case MONDAY:
                currentNumericDay = 1;
                break;
            case TUESDAY:
                currentNumericDay = 2;
                break;
            case WEDNESDAY:
                currentNumericDay = 3;
                break;
            case THURSDAY:
                currentNumericDay = 4;
                break;
            case FRIDAY:
                currentNumericDay = 5;
                break;
            case SATURDAY:
                currentNumericDay = 6;
                break;

        }
        return currentNumericDay;

    }

    public LiveData<PlaceSearchResults> getPlaceSearchResultsLiveData() { return placeSearchResultsLiveData; }

    public LiveData<Location> getLocationUseCase() { return locationLiveData; }

    public void startLocationRequest() { locationRepository.StartLocationRequest(); }

}
