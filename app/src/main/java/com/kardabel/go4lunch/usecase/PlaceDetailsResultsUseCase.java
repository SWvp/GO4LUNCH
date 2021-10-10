package com.kardabel.go4lunch.usecase;

import android.location.Location;

import androidx.annotation.Nullable;
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
import java.util.Map;

public class PlaceDetailsResultsUseCase {

    private MediatorLiveData<List<PlaceDetailsResult>> placeDetailsMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<PlaceDetailsResult> mPlaceDetailsResultMediatorLiveData = new MediatorLiveData<>();
    private LiveData<List<PlaceDetailsResult>> placeDetailsLiveData;
    private List<PlaceDetailsResult> listPlaceDetailsResults = new ArrayList<>();
    private LiveData<NearbySearchResults> mPlaceSearchResultsLiveData;
    public LiveData<NearbySearchResults> nearbySearchResultsLiveData;
    private LiveData<String> mStringMediatorLiveData = new MediatorLiveData<>();
    private String locationString;
    private PlaceSearchResponseRepository mPlaceSearchResponseRepository;

    public PlaceDetailsResultsUseCase(LocationRepository locationRepository,
                                      PlaceSearchResponseRepository placeSearchResponseRepository,
                                      PlaceDetailsResponseRepository placeDetailsResponseRepository) {
        this.mPlaceDetailsResponseRepository = placeDetailsResponseRepository;


        //LiveData<PlaceDetailsResult> placeDetailsResultLiveData = placeDetailsResponseRepository.getRestaurantDetailsLiveData(placeId);


       nearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbySearchResults>>() {
           @Override
           public LiveData<PlaceSearchResults> apply(Location input) {
               String locationAsText = input.getLatitude() + "," + input.getLongitude();
               return placeSearchResponseRepository.getRestaurantListLiveData(
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

        placeDetailsMediatorLiveData.addSource(mPlaceDetailsResultMediatorLiveData, new Observer<PlaceDetailsResult>() {
            @Override
            public void onChanged(PlaceDetailsResult placeDetailsResult) {
                combine(nearbySearchResultsLiveData.getValue(), placeDetailsResult);

            }
        });







 //     mPlaceDetailsResultMediatorLiveData.addSource(nearbySearchResultsLiveData, new Observer<NearbySearchResults>() {
 //       @Override
 //       public void onChanged(NearbySearchResults nearbySearchResults) {
 //           //combine(nearbySearchResults, placeDetailsResultLiveData.getValue());
 //           for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
 //               placeId = nearbySearchResults.getResults().get(i).getPlaceId();
 //               placeIdList.add(placeId);
 //               //mPlaceDetailsResponseRepository.getRestaurantDetailsLiveData(placeId);
 //             // placeDetailsMediatorLiveData.addSource(placeDetailsResultLiveData, new Observer<PlaceDetailsResult>() {
 //             //     @Override
 //             //     public void onChanged(PlaceDetailsResult placeDetailsResult) {
 //             //         listPlaceDetailsResults.add(placeDetailsResult);
 //             //     }
 //             // });

 //           }
 //           mListLiveData.setValue(placeIdList);
 //           //placeDetailsMediatorLiveData.setValue(listPlaceDetailsResults);

 //       }
 //   });

 //     mPlaceDetailsResultMediatorLiveData.addSource(mListLiveData, new Observer<List<String>>() {
 //       @Override
 //       public void onChanged(List<String> strings) {
 //           for (String string: strings) {
 //               placeId = string;

 //               mPlaceDetailsResultMediatorLiveData.setValue(placeDetailsResultLiveData.getValue());
 //           }

 //       }
 //   });



















 //    placeDetailsMediatorLiveData.addSource(placeDetailsResultLiveData, new Observer<PlaceDetailsResult>() {
 //        @Override
 //        public void onChanged(PlaceDetailsResult placeDetailsResult) {
 //            combine(nearbySearchResultsLiveData.getValue(), placeDetailsResult);

 //        }
 //    });

 // }

 // private void combine(NearbySearchResults nearbySearchResults, PlaceDetailsResult placeDetailsResult) {

 //     if (nearbySearchResults != null) {
 //         for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
 //             placeId = nearbySearchResults.getResults().get(i).getPlaceId();
 //             mPlaceDetailsResponseRepository.getRestaurantDetailsLiveData(placeId);


 //         }
 //     }
 //     else{

 //         listPlaceDetailsResults.add(placeDetailsResult);
 //         placeDetailsMediatorLiveData.setValue(listPlaceDetailsResults);

 //     }



    }

    private void combine(NearbySearchResults nearbySearchResults, @Nullable PlaceDetailsResult placeDetailsResult ) {
        if (nearbySearchResults != null) {
            for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
                String placeId = nearbySearchResults.getResults().get(i).getPlaceId();
                if(!listPlaceDetailsResults.contains(placeDetailsResult) || placeDetailsResult == null){
                    mPlaceDetailsResultMediatorLiveData.addSource(mPlaceDetailsResponseRepository.getRestaurantDetailsLiveData(placeId), new Observer<PlaceDetailsResult>() {
                        @Override
                        public void onChanged(PlaceDetailsResult placeDetailsResult) {
                            listPlaceDetailsResults.add(placeDetailsResult);
                            mPlaceDetailsResultMediatorLiveData.setValue(placeDetailsResult);
                        }
                    });
                }


            }
            placeDetailsMediatorLiveData.setValue(listPlaceDetailsResults);
        }
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

    public LiveData <List<PlaceDetailsResult>> getPlaceDetailsResultLiveData() {
        return placeDetailsMediatorLiveData; }



}