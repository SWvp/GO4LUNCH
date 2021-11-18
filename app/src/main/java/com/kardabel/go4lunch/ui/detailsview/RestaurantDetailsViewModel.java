package com.kardabel.go4lunch.ui.detailsview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWithFavoriteRestaurant;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.usecase.ChangeFavoriteStateUseCase;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsByIdUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsByIdUseCase;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsViewModel extends ViewModel {

    private final MediatorLiveData<RestaurantDetailsViewState> workMatesDetailsViewStateMediatorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<DetailsWorkmatesViewState>> workmatesLikeThisRestaurantMediatorLiveData = new MediatorLiveData<>();
    private RestaurantDetailsViewState result;
    @NonNull
    private final GetNearbySearchResultsByIdUseCase getNearbySearchResultsByIdUseCase;
    @NonNull
    private final GetRestaurantDetailsResultsByIdUseCase getRestaurantDetailsResultsByIdUseCase;
    @NonNull
    private final WorkmatesRepository workmatesRepository;


    public RestaurantDetailsViewModel(@NonNull GetNearbySearchResultsByIdUseCase getNearbySearchResultsByIdUseCase,
                                      @NonNull GetRestaurantDetailsResultsByIdUseCase getRestaurantDetailsResultsByIdUseCase,
                                      @NonNull WorkmatesRepository workmatesRepository){
        this.getNearbySearchResultsByIdUseCase = getNearbySearchResultsByIdUseCase;
        this.getRestaurantDetailsResultsByIdUseCase = getRestaurantDetailsResultsByIdUseCase;
        this.workmatesRepository = workmatesRepository;

    }

    public void init(String placeId){
        LiveData<RestaurantSearch> restaurantLiveData = getNearbySearchResultsByIdUseCase.invoke(placeId);
        LiveData<RestaurantDetailsResult> restaurantDetailsLiveData = getRestaurantDetailsResultsByIdUseCase.invoke(placeId);

        LiveData<List<UserWithFavoriteRestaurant>> userWithFavoriteRestaurantLiveData = workmatesRepository.getRestaurantsAddAsFavorite();
        LiveData<List<UserModel>> workMatesLiveData = workmatesRepository.getWorkmates();


        // OBSERVERS FOR RESTAURANT DETAILS

        workMatesDetailsViewStateMediatorLiveData.addSource(restaurantLiveData, restaurant -> combine(
                restaurant,
                userWithFavoriteRestaurantLiveData.getValue(),
                restaurantDetailsLiveData.getValue()));

        workMatesDetailsViewStateMediatorLiveData.addSource(userWithFavoriteRestaurantLiveData, userWithFavoriteRestaurants -> combine(
                restaurantLiveData.getValue(),
                userWithFavoriteRestaurants,
                restaurantDetailsLiveData.getValue()));

        workMatesDetailsViewStateMediatorLiveData.addSource(restaurantDetailsLiveData, restaurantDetailsResults -> combine(
                restaurantLiveData.getValue(),
                userWithFavoriteRestaurantLiveData.getValue(),
                restaurantDetailsResults));




        // OBSERVERS FOR WORKMATES RECYCLERVIEW


        workmatesLikeThisRestaurantMediatorLiveData.addSource(restaurantLiveData, new Observer<RestaurantSearch>() {
            @Override
            public void onChanged(RestaurantSearch restaurantSearch) {
                combineWorkmates(restaurantSearch, userWithFavoriteRestaurantLiveData.getValue(),workMatesLiveData.getValue());
            }
        });
        workmatesLikeThisRestaurantMediatorLiveData.addSource(userWithFavoriteRestaurantLiveData, userWithFavoriteRestaurants -> combineWorkmates(
                restaurantLiveData.getValue(),
                userWithFavoriteRestaurants,
                workMatesLiveData.getValue()));
        workmatesLikeThisRestaurantMediatorLiveData.addSource(workMatesLiveData, userModels -> combineWorkmates(
                restaurantLiveData.getValue(),
                userWithFavoriteRestaurantLiveData.getValue(),
                userModels));

    }

    // COMBINE FIRST MEDIATOR FOR RESTAURANT DETAILS
    private void combine(@Nullable RestaurantSearch restaurant,
                         @Nullable List<UserWithFavoriteRestaurant> usersWithFavoriteRestaurant,
                         @Nullable RestaurantDetailsResult restaurantDetails) {
        if (restaurant != null && restaurantDetails == null) {
            workMatesDetailsViewStateMediatorLiveData.setValue(mapWithoutDetails(restaurant, usersWithFavoriteRestaurant));
        }else if (restaurant != null){
            workMatesDetailsViewStateMediatorLiveData.setValue(map(restaurant,restaurantDetails ,usersWithFavoriteRestaurant));
        }
    }

    // MAP WITHOUT DETAILS, WHEN USER HAS NO LONGER NETWORK AVAILABLE
    // (ASSUME THAT THE GOOGLE DETAILS SERVICE WILL WORK AS LONG AS NEARBY WORKS)
    private RestaurantDetailsViewState mapWithoutDetails(@NonNull RestaurantSearch restaurant,
                                                         List<UserWithFavoriteRestaurant> usersWithFavoriteRestaurant) {

        List<String> restaurantAsFavoriteId = new ArrayList<>();

        if (usersWithFavoriteRestaurant != null) {
            for (int i = 0; i < usersWithFavoriteRestaurant.size(); i++) {
                restaurantAsFavoriteId.add(usersWithFavoriteRestaurant.get(i).getRestaurantId());

            }
        }
        boolean isFavorite = false;
        if(restaurantAsFavoriteId.contains(restaurant.getRestaurantId())){
            isFavorite = true;
        }
        result = new RestaurantDetailsViewState(
                restaurant.getRestaurantName(),
                restaurant.getRestaurantAddress(),
                photoReference(restaurant.getRestaurantPhotos()),
                "",
                "",
                restaurant.getRestaurantId(),
                convertRatingStars(restaurant.getRating()),
                isFavorite

        );
        return result;

    }

    // MAP WITH RESTAURANT DETAILS
    private RestaurantDetailsViewState map(@NonNull RestaurantSearch restaurant,
                                           @NonNull RestaurantDetailsResult restaurantDetails,
                                           List<UserWithFavoriteRestaurant> usersWithFavoriteRestaurant) {

        List<String> restaurantAsFavoriteId = new ArrayList<>();
        String restaurantPhoneNumber = "";
        String restaurantWebsite = "";

        if (usersWithFavoriteRestaurant != null) {
            for (int i = 0; i < usersWithFavoriteRestaurant.size(); i++) {
                restaurantAsFavoriteId.add(usersWithFavoriteRestaurant.get(i).getRestaurantId());

            }
        }
        if (restaurantDetails.getDetailsResult().getFormatted_phone_number() != null) {
            restaurantPhoneNumber = restaurantDetails.getDetailsResult().getFormatted_phone_number();

        }
        if (restaurantDetails.getDetailsResult().getWebsite() != null) {
            restaurantWebsite = restaurantDetails.getDetailsResult().getWebsite();

        }

        boolean isFavorite = false;
        if (restaurantAsFavoriteId.contains(restaurant.getRestaurantId())) {
            isFavorite = true;
        }
        result = new RestaurantDetailsViewState(
                restaurant.getRestaurantName(),
                restaurant.getRestaurantAddress(),
                photoReference(restaurant.getRestaurantPhotos()),
                restaurantPhoneNumber,
                restaurantWebsite,
                restaurant.getRestaurantId(),
                convertRatingStars(restaurant.getRating()),
                isFavorite

        );
        return result;

    }

    // COMBINE SECOND MEDIATOR FOR WORKMATES WITH THIS RESTAURANT IN FAVORITE
    private void combineWorkmates(@Nullable RestaurantSearch restaurant,
                                  @Nullable List<UserWithFavoriteRestaurant> usersWithFavoriteRestaurant,
                                  @Nullable List<UserModel> users) {

        if (usersWithFavoriteRestaurant != null && users != null && restaurant != null) {
            workmatesLikeThisRestaurantMediatorLiveData.setValue(mapWorkmates(
                    restaurant,
                    usersWithFavoriteRestaurant,
                    users));
        }
    }

    // MAP THE RECYCLER VIEW ITEMS FOR WORKMATES WHO HAVE THIS ITEM IN FAVORITE
    private List<DetailsWorkmatesViewState> mapWorkmates(RestaurantSearch restaurant,
                                                         List<UserWithFavoriteRestaurant> userWithFavoriteRestaurants,
                                                         List<UserModel> users){

        List<DetailsWorkmatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < userWithFavoriteRestaurants.size(); i++) {
            if (userWithFavoriteRestaurants.get(i).getRestaurantId().equals(restaurant.getRestaurantId())) {
                for (int j = 0; j < users.size(); j++) {
                    if (users.get(j).getUid().equals(userWithFavoriteRestaurants.get(i).getUserId())) {
                        String name = users.get(j).getUserName() + " is joining!";
                        String avatar = users.get(j).getAvatarURL();

                        workMatesViewStateList.add(new DetailsWorkmatesViewState(
                                name,
                                avatar

                        ));

                    }
                }
            }
        }
        return workMatesViewStateList;

    }

    // SEARCH FOR A PHOTO IN THE LIST PROVIDED BY NEARBY PLACES
    private String photoReference(List<Photo> photoList) {
        if (photoList != null) {
            for (Photo photo : photoList) {
                if (!photo.getPhotoReference().isEmpty()) {
                    return photo.getPhotoReference();

                }
            }
        }
        return null;

    }

    private double convertRatingStars(double rating) {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return Math.round(rating * 3 / 5);

    }

    // SAY TO FIRESTORE THIS RESTAURANT IS ON FAVORITE OR NOT
    public void onFavoriteClick(String restaurantId, String restaurantName){
        //firestoreUseCase.onFavoriteClick(restaurantId, restaurantName);
        ChangeFavoriteStateUseCase.onFavoriteClick(restaurantId, restaurantName);

    }

    public LiveData<RestaurantDetailsViewState> getRestaurantDetailsViewStateLiveData(){
        return workMatesDetailsViewStateMediatorLiveData;

    }

    public LiveData<List<DetailsWorkmatesViewState>> getWorkmatesLikeThisRestaurant() {
        return workmatesLikeThisRestaurantMediatorLiveData;

    }
}
