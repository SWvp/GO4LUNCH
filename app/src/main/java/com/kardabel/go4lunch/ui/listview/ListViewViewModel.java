package com.kardabel.go4lunch.ui.listview;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.NearbyResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbyResponseRepository;

import java.util.ArrayList;
import java.util.List;

public class ListViewViewModel extends ViewModel {

    private LiveData<RestaurantsWrapperViewState> listViewViewStateLiveData;
    private Location userLocation;

    public ListViewViewModel(@NonNull Application application, @Nullable LocationRepository locationRepository, @Nullable NearbyResponseRepository nearbyResponseRepository) {
        super();

        // TRANSFORM A LOCATION IN A REQUEST ON WEB WITH NEARBY PLACES
        assert locationRepository != null;
        LiveData<NearbyResults> nearbyResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbyResults>>() {
                    @Override
                    public LiveData<NearbyResults> apply(Location input) {
                        userLocation = input;
                        String locationAsText = input.getLatitude() + "," + input.getLongitude();
                        assert nearbyResponseRepository != null;
                        return nearbyResponseRepository.getRestaurantListLiveData("AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8", "restaurant", locationAsText, "1000");

                    }
                }
        );

        // UPDATE LIVEDATA WITH MAP FUNCTION
        listViewViewStateLiveData = Transformations.map(nearbyResultsLiveData, new Function<NearbyResults, RestaurantsWrapperViewState>() {
            @Override
            public RestaurantsWrapperViewState apply(NearbyResults input) {
                return map(input);
            }
        });
    }

    // CREATE A LIST OF RESTAURANTS ITEMS WITH NEARBY RESULTS
    private RestaurantsWrapperViewState map(NearbyResults nearbyResults){
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < nearbyResults.getResults().size(); i++){
            String restaurantName = nearbyResults.getResults().get(i).getRestaurantName();
            String addressRestaurant = nearbyResults.getResults().get(i).getRestaurantAddress();
            String photoRestaurant = photoReference(nearbyResults.getResults().get(i).getRestaurantPhotos());
            String distanceRestaurant = distance(nearbyResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(), nearbyResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            String openingHoursRestaurant = convertOpeningHours();
            Double ratingRestaurant = convertRatingStars(nearbyResults.getResults().get(i).getRating());
            String placeIdRestaurant = nearbyResults.getResults().get(i).getPlaceId();

            restaurantList.add(new RestaurantItemViewState(restaurantName, addressRestaurant, photoRestaurant, distanceRestaurant, openingHoursRestaurant, ratingRestaurant, placeIdRestaurant));

        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    // SEARCH FOR A PHOTO IN THE LIST PROVIDED BY NEARBY PLACES
    private String photoReference(List<Photo> photoList){
        if (photoList != null){
            for (Photo photo : photoList){
                if (!photo.getPhotoReference().isEmpty()){
                    return photo.getPhotoReference();

                }
            }
        }
        return null;

    }

    // CONVERT OPENING HOURS
    private String convertOpeningHours(){
        String noHoursDetails = "Opening hours unavailable !";
        return noHoursDetails;

    }

    // GET LOCATION DISTANCE FROM USER TO RESTAURANT
    private String distance(double lat, double lng){
        Location restaurantLocation = new Location("restaurant location");
        restaurantLocation.setLatitude(lat);
        restaurantLocation.setLongitude(lng);
        float distance =userLocation.distanceTo(restaurantLocation);
        return (int) distance + "m";

    }

    // CONVERT RATING STARS (5 -> 3)
    private double convertRatingStars(double rating) {

        long convertedRating = Math.round(rating * 3 / 5);

        if (convertedRating <= 0) {
            return 0;
        } else if (convertedRating < 1.5) {
            return 1;
        } else if (convertedRating >= 1.5 && convertedRating <= 2.5) {
            return 2;
        } else {
            return 3;
        }

    }

    // LIVEDATA OBSERVED BY LISTVIEWFRAGMENT
    public LiveData<RestaurantsWrapperViewState> getListViewViewStateLiveData() {
        return listViewViewStateLiveData;

    }
}