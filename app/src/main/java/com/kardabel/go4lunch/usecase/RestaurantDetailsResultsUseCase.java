package com.kardabel.go4lunch.usecase;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.RestaurantDetailsResponseRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantDetailsResultsUseCase {

    private MediatorLiveData<List<RestaurantDetailsResult>> placeDetailsMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<RestaurantDetailsResult> mPlaceDetailsResultMediatorLiveData = new MediatorLiveData<>();
    private LiveData<List<RestaurantDetailsResult>> placeDetailsLiveData;
    private List<RestaurantDetailsResult> mListRestaurantDetailsResults = new ArrayList<>();
    private LiveData<NearbySearchResults> mPlaceSearchResultsLiveData;
    public LiveData<NearbySearchResults> nearbySearchResultsLiveData;
    private LiveData<String> mStringMediatorLiveData = new MediatorLiveData<>();
    //private String placeId;
    private List<String> placeIdList;
    private MutableLiveData<List<String>> mListLiveData;
    private RestaurantDetailsResponseRepository mRestaurantDetailsResponseRepository;
    private Map<String, NearbySearchResults> cache;


    public RestaurantDetailsResultsUseCase(LocationRepository locationRepository,
                                           NearbySearchResponseRepository nearbySearchResponseRepository,
                                           RestaurantDetailsResponseRepository restaurantDetailsResponseRepository) {
        this.mRestaurantDetailsResponseRepository = restaurantDetailsResponseRepository;


        //LiveData<PlaceDetailsResult> placeDetailsResultLiveData = placeDetailsResponseRepository.getRestaurantDetailsLiveData(placeId);


       nearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbySearchResults>>() {
           @Override
           public LiveData<NearbySearchResults> apply(Location input) {
               String locationAsText = input.getLatitude() + "," + input.getLongitude();
               return nearbySearchResponseRepository.getRestaurantListLiveData(
                       "restaurant",
                       locationAsText,
                       "1000");

           }
       });

        placeDetailsMediatorLiveData.addSource(nearbySearchResultsLiveData, new Observer<NearbySearchResults>() {
           @Override
           public void onChanged(NearbySearchResults nearbySearchResults) {
               combine(nearbySearchResults, mPlaceDetailsResultMediatorLiveData.getValue());
           }
       });

        placeDetailsMediatorLiveData.addSource(mPlaceDetailsResultMediatorLiveData, new Observer<RestaurantDetailsResult>() {
            @Override
            public void onChanged(RestaurantDetailsResult restaurantDetailsResult) {
                combine(nearbySearchResultsLiveData.getValue(), restaurantDetailsResult);

            }
        });
    }

    private void combine(NearbySearchResults nearbySearchResults, @Nullable RestaurantDetailsResult restaurantDetailsResult) {
        if (nearbySearchResults != null) {
            for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
                String placeId = nearbySearchResults.getResults().get(i).getPlaceId();
                //placeId = "ChIJeSBfyaavthIR7ClPRsEYun0";
                if(!mListRestaurantDetailsResults.contains(restaurantDetailsResult) || restaurantDetailsResult == null){
                    mPlaceDetailsResultMediatorLiveData.addSource(mRestaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId), new Observer<RestaurantDetailsResult>() {
                        @Override
                        public void onChanged(RestaurantDetailsResult restaurantDetailsResult) {
                            mListRestaurantDetailsResults.add(restaurantDetailsResult);
                            mPlaceDetailsResultMediatorLiveData.setValue(restaurantDetailsResult);

                        }
                    });
                }
            }
            placeDetailsMediatorLiveData.setValue(mListRestaurantDetailsResults);

        }
    }

    public LiveData <List<RestaurantDetailsResult>> getPlaceDetailsResultLiveData() {
        return placeDetailsMediatorLiveData;
    }

}