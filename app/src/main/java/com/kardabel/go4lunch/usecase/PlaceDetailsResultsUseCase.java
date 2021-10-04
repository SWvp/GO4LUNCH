package com.kardabel.go4lunch.usecase;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.pojo.RestaurantDetails;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.PlaceDetailsResponseRepository;
import com.kardabel.go4lunch.repository.PlaceSearchResponseRepository;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailsResultsUseCase {

    private LiveData<List<PlaceDetailsResult>> placeDetails;
    private List<PlaceDetailsResult> mPlaceDetailsResults = new ArrayList<>();
    private LiveData<PlaceSearchResults> mPlaceSearchResultsLiveData;
    public LiveData<PlaceSearchResults> placeSearchResultsLiveData;
    private LiveData<String> mStringMediatorLiveData = new MediatorLiveData<>();
    private String locationString;
    private PlaceSearchResponseRepository mPlaceSearchResponseRepository;

    public PlaceDetailsResultsUseCase(LocationRepository locationRepository,
                                      PlaceSearchResponseRepository placeSearchResponseRepository,
                                      PlaceDetailsResponseRepository placeDetailsResponseRepository) {



 //     placeDetails = Transformations.map(placeSearchResponseRepository.getPlaceSearchResultsLiveData(), new Function<PlaceSearchResults, List<PlaceDetailsResult>>() {
 //         @Override
 //         public List<PlaceDetailsResult> apply(PlaceSearchResults input) {
 //             for (int i = 0; i < input.getResults().size(); i++){
 //                 String placeId = input.getResults().get(i).getPlaceId();
 //                 LiveData<PlaceDetailsResult> details = placeDetailsResponseRepository.getRestaurantDetailsLiveData(placeId);
 //                 placeDetailsResults.add(details.getValue());

 //             }
 //             return placeDetailsResults;
 //         }
 //     });



 //     location = Transformations.map(locationRepository.getLocationLiveData(), new Function<Location, String>() {
 //         @Override
 //         public String apply(Location input) {

 //             return map(input);
 //         }
 //     });

 //     placeDetails = Transformations.switchMap(placeSearchResultsUseCase.getPlaceSearchResultsLiveData(), new Function<PlaceSearchResults, LiveData<List<PlaceDetailsResult>>>() {
 //         @Override
 //         public LiveData<List<PlaceDetailsResult>> apply(PlaceSearchResults input) {
 //             for (int i = 0; i < input.getResults().size(); i++){
 //                 String place_id = input.getResults().get(i).getPlaceId();
 //                 placeDetails.getValue().add(placeDetailsResponseRepository.getRestaurantDetailsLiveData(place_id).getValue());
 //             }
 //             return placeDetails;
 //         }
 //     });


       placeSearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<PlaceSearchResults>>() {
           @Override
           public LiveData<PlaceSearchResults> apply(Location input) {
               String locationAsText = input.getLatitude() + "," + input.getLongitude();
               return placeSearchResponseRepository.getRestaurantListLiveData(
                       "restaurant",
                       locationAsText,
                       "1000");

           }
       });

       placeDetails = Transformations.map(placeSearchResultsLiveData, new Function<PlaceSearchResults, List<PlaceDetailsResult>>() {
           @Override
           public List<PlaceDetailsResult> apply(PlaceSearchResults input) {
               for (int i = 0; i < input.getResults().size(); i++) {
                   String place_id = input.getResults().get(i).getPlaceId();
                   mPlaceDetailsResults.add(placeDetailsResponseRepository.getRestaurantDetailsLiveData(place_id).getValue());

               }
               return mPlaceDetailsResults;
           }
       });


   }



 //     mPlaceSearchResultsLiveData = Transformations.switchMap(placeSearchResultsUseCase.getLocationString(), new Function<String, LiveData<PlaceSearchResults>>() {
 //                 @Override
 //                 public LiveData<PlaceSearchResults> apply(String input) {
 //                     MutableLiveData<PlaceSearchResults> placeSearchResultsMutableLiveData = new MutableLiveData<>();
 //                     placeSearchResultsMutableLiveData.setValue(placeSearchResponseRepository.getRestaurantListLiveData(
 //                             "restaurant",
 //                             input,
 //                             "1000").getValue());
 //                     return placeSearchResultsMutableLiveData;
 //                 }
 //             });
 //     placeDetails = Transformations.switchMap(mPlaceSearchResultsLiveData, new Function<PlaceSearchResults, LiveData<List<PlaceDetailsResult>>>() {
 //         @Override
 //         public LiveData<List<PlaceDetailsResult>> apply(PlaceSearchResults input) {
 //             for (int i = 0; i < input.getResults().size(); i++) {
 //                 String place_id = input.getResults().get(i).getPlaceId();
 //                 placeDetails.getValue().add(placeDetailsResponseRepository.getRestaurantDetailsLiveData(place_id).getValue());


 //             }
 //             return placeDetails;
 //         }
 //     });





 //            placeDetails = Transformations.switchMap(placeSearchResponseRepository
 //                    .getRestaurantListLiveData("restaurant",
 //                            locationString,
 //                            "1000"), new Function<PlaceSearchResults, LiveData<List<PlaceDetailsResult>>>() {
 //                @Override
 //                public LiveData<List<PlaceDetailsResult>> apply(PlaceSearchResults input) {
 //                    for (int i = 0; i < input.getResults().size(); i++) {
 //                        String place_id = input.getResults().get(i).getPlaceId();
 //                        placeDetails.getValue().add(placeDetailsResponseRepository.getRestaurantDetailsLiveData(place_id).getValue());


 //                    }
 //                    return placeDetails;
 //                }
 //            });





// private String map(Location input) {
//     locationString = input.getLatitude() + "," + input.getLongitude();
//     mStringMediatorLiveData.setValue(locationString);
//     return locationString;
// }

 // private String getLocation(){
 //     locationString = (locationRepository.getLocationLiveData().getValue().getLatitude() + "," + locationRepository.getLocationLiveData().getValue().getLongitude());
 //     return locationString;
 // }

    public LiveData <List<PlaceDetailsResult>> getPlaceDetailsResultLiveData() { return placeDetails; }



}