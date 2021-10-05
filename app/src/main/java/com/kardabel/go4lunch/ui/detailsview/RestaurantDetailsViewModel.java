package com.kardabel.go4lunch.ui.detailsview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;

import java.util.List;

public class RestaurantDetailsViewModel extends ViewModel {

    private String placeId = "";

    private LiveData<RestaurantDetailsViewState> restaurantDetailsViewStateLiveData;
    private RestaurantDetailsViewState result;

    public RestaurantDetailsViewModel(@Nullable NearbySearchResultsUseCase nearbySearchResultsUseCase){

        // UPDATE LIVEDATA WITH MAP FUNCTION
        //listViewViewStateLiveData = Transformations.map(nearbyResultsLiveData, input -> map(input));
        restaurantDetailsViewStateLiveData = Transformations.map(nearbySearchResultsUseCase.getNearbySearchResultsLiveData(), new Function<NearbySearchResults, RestaurantDetailsViewState>() {
            @Override
            public RestaurantDetailsViewState apply(NearbySearchResults input) {
                return RestaurantDetailsViewModel.this.map(input);
            }
        });


    }

    private RestaurantDetailsViewState map(@NonNull NearbySearchResults nearbySearchResults) {

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
            if(placeId.equals(nearbySearchResults.getResults().get(i).getPlaceId())){

                result = new RestaurantDetailsViewState(
                        nearbySearchResults.getResults().get(i).getRestaurantName(),
                        nearbySearchResults.getResults().get(i).getRestaurantAddress(),
                        photoReference(nearbySearchResults.getResults().get(i).getRestaurantPhotos()),
                        "23 48 23 48",
                        "www.ouioui.com"

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
