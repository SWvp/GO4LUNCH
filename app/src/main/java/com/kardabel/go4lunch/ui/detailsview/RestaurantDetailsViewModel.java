package com.kardabel.go4lunch.ui.detailsview;

import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.usecase.NearbyResultsUseCase;

import java.util.List;

public class RestaurantDetailsViewModel extends ViewModel {

    private String placeId = "";

    private LiveData<RestaurantDetailsViewState> restaurantDetailsViewStateLiveData;
    private RestaurantDetailsViewState result;

    public RestaurantDetailsViewModel(@Nullable NearbyResultsUseCase nearbyResultsUseCase){

        // UPDATE LIVEDATA WITH MAP FUNCTION
        //listViewViewStateLiveData = Transformations.map(nearbyResultsLiveData, input -> map(input));
        restaurantDetailsViewStateLiveData = Transformations.map(nearbyResultsUseCase.getPlaceSearchResultsLiveData(), input -> map(input));


    }

    private RestaurantDetailsViewState map(@NonNull PlaceSearchResults placeSearchResults) {

        for (int i = 0; i < placeSearchResults.getResults().size(); i++) {
            if(placeId.equals(placeSearchResults.getResults().get(i).getPlaceId())){

                result = new RestaurantDetailsViewState(
                        placeSearchResults.getResults().get(i).getRestaurantName(),
                        placeSearchResults.getResults().get(i).getRestaurantAddress(),
                        photoReference(placeSearchResults.getResults().get(i).getRestaurantPhotos()),
                        placeSearchResults.getResults().get(i).getRestaurantNumber(),
                        placeSearchResults.getResults().get(i).getWebsite()

                );
            }
        }
        return result;

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

    public void init(String placeId){
        this.placeId = placeId;
    }

    public LiveData<RestaurantDetailsViewState> getRestaurantDetailsViewStateLiveData(){
        return restaurantDetailsViewStateLiveData;

    }
}
