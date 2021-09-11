package com.kardabel.go4lunch.ui.listview;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.NearbyResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbyResponseRepository;

import java.util.ArrayList;
import java.util.List;

public class ListViewViewModel extends ViewModel {

    private final MediatorLiveData<List<ListViewRestaurantViewState>> listViewViewStateMediatorLiveData = new MediatorLiveData<>();

    private LiveData<ListViewViewState> listViewViewStateLiveData;

    //private LocationRepository locationRepository = new LocationRepository();
    //private NearbyResponseRepository mNearbyResponseRepository ;
    private Location userLocation;

    public ListViewViewModel(@NonNull Application application, @Nullable LocationRepository locationRepository, @Nullable NearbyResponseRepository nearbyResponseRepository) {

        super();


        // Transform a location in a request on web via nearby places
        LiveData<NearbyResults> nearbyResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbyResults>>() {
                    @Override
                    public LiveData<NearbyResults> apply(Location input) {
                        userLocation = input;
                        String locationAsText = input.getLatitude() + "," + input.getLongitude();
                        return nearbyResponseRepository.getRestaurantListLiveData("AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8", "restaurant", locationAsText, "1000");

                    }
                }
        );

        listViewViewStateLiveData = Transformations.map(nearbyResultsLiveData, new Function<NearbyResults, ListViewViewState>() {
            @Override
            public ListViewViewState apply(NearbyResults input) {
                return map(input);
            }
        });
    }

    private void combine(){ }



    private ListViewViewState map(NearbyResults nearbyResults){
        List<ListViewRestaurantViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < nearbyResults.getResults().size(); i++){
            String restaurantName = nearbyResults.getResults().get(i).getRestaurantName();
            String addressRestaurant = nearbyResults.getResults().get(i).getRestaurantAddress();
            String photoRestaurant = photoReference(nearbyResults.getResults().get(i).getRestaurantPhotos());
            String distanceRestaurant = distance();
            String openingHoursRestaurant = convertOpeningHours();
            Double ratingRestaurant = convertRatingStars(nearbyResults.getResults().get(i).getRating());
            String placeIdRestaurant = nearbyResults.getResults().get(i).getPlaceId();

            restaurantList.add(new ListViewRestaurantViewState(restaurantName, addressRestaurant, photoRestaurant, distanceRestaurant, openingHoursRestaurant, ratingRestaurant, placeIdRestaurant));

        }

        return new ListViewViewState(restaurantList);


//     for (RestaurantDetails restaurantDetails : restaurantDetailsList)
//         result.add(new ListViewViewState(
//                 restaurantDetails.getRestaurantName(),
//                 restaurantDetails.getRestaurantAddress(),
//                 photoReference(restaurantDetails.getRestaurantPhotos()),
//                 distance(),
//                 convertOpeningHours(),
//                 convertRatingStars(restaurantDetails.getRating()),
//                 restaurantDetails.getPlaceId()

//         ));
//     return result;

    }



    // CONVERT RATING STARS (5 -> 3)
    private double convertRatingStars(double rating){
        return 0;

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
    private String distance(){
        int distance = 0;

        return distance + "m";
    }

    public LiveData<ListViewViewState> getListViewViewStateLiveData() {
        return listViewViewStateLiveData;
    }
}