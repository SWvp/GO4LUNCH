package com.kardabel.go4lunch.ui.detailsview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWithFavoriteRestaurant;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.usecase.ChangeFavoriteStateUseCase;
import com.kardabel.go4lunch.usecase.FirestoreUseCase;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsViewModel extends ViewModel {

    private String placeId = "";
    private final MediatorLiveData<RestaurantDetailsViewState> workMatesDetailsViewStateMediatorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<DetailsWorkmatesViewState>> workmatesLikeThisRestaurantMediatorLiveData = new MediatorLiveData<>();
    private RestaurantDetailsViewState result;
    private final FirestoreUseCase firestoreUseCase;


    public RestaurantDetailsViewModel(@NonNull GetNearbySearchResultsUseCase getNearbySearchResultsUseCase,
                                      @NonNull GetRestaurantDetailsResultsUseCase getRestaurantDetailsResultsUseCase,
                                      @NonNull FirestoreUseCase firestoreUseCase,
                                      @NonNull WorkmatesRepository workmatesRepository){

        this.firestoreUseCase = firestoreUseCase;

        LiveData<NearbySearchResults> nearbySearchResultsLiveData = getNearbySearchResultsUseCase.getNearbySearchResultsLiveData();
        LiveData<List<RestaurantDetailsResult>> restaurantDetailsResultsUseCaseLiveData = getRestaurantDetailsResultsUseCase.getPlaceDetailsResultLiveData();
        LiveData<List<UserWithFavoriteRestaurant>> userWithFavoriteRestaurantLiveData = workmatesRepository.getRestaurantsAddAsFavorite();
        LiveData<List<UserModel>> workMatesLiveData = workmatesRepository.getWorkmates();


        // OBSERVERS

        workMatesDetailsViewStateMediatorLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults -> combine(
                nearbySearchResults,
                userWithFavoriteRestaurantLiveData.getValue(),
                restaurantDetailsResultsUseCaseLiveData.getValue()));

        workMatesDetailsViewStateMediatorLiveData.addSource(userWithFavoriteRestaurantLiveData, userWithFavoriteRestaurants -> combine(
                nearbySearchResultsLiveData.getValue(),
                userWithFavoriteRestaurants,
                restaurantDetailsResultsUseCaseLiveData.getValue()));

        workMatesDetailsViewStateMediatorLiveData.addSource(restaurantDetailsResultsUseCaseLiveData, restaurantDetailsResults -> combine(
                nearbySearchResultsLiveData.getValue(),
                userWithFavoriteRestaurantLiveData.getValue(),
                restaurantDetailsResults));


        // OBSERVERS FOR WORKMATES RECYCLERVIEW

        workmatesLikeThisRestaurantMediatorLiveData.addSource(userWithFavoriteRestaurantLiveData, new Observer<List<UserWithFavoriteRestaurant>>() {
            @Override
            public void onChanged(List<UserWithFavoriteRestaurant> userWithFavoriteRestaurants) {
                mapWorkmates(userWithFavoriteRestaurants, workMatesLiveData.getValue());

            }
        });

        workmatesLikeThisRestaurantMediatorLiveData.addSource(workMatesLiveData, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                mapWorkmates(userWithFavoriteRestaurantLiveData.getValue(), userModels);

            }
        });
    }

    private void mapWorkmates(List<UserWithFavoriteRestaurant> userWithFavoriteRestaurants, List<UserModel> users) {
        if (userWithFavoriteRestaurants != null && users != null) {
            List<DetailsWorkmatesViewState> workMatesViewStateList = new ArrayList<>();
            for (int i = 0; i < userWithFavoriteRestaurants.size(); i++) {
                if (userWithFavoriteRestaurants.get(i).getRestaurantId().equals(placeId)) {
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
            workmatesLikeThisRestaurantMediatorLiveData.setValue(workMatesViewStateList);
        }
    }


    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable List<UserWithFavoriteRestaurant> usersWithFavoriteRestaurant,
                         @Nullable List<RestaurantDetailsResult> restaurantDetailsResults) {
        if (nearbySearchResults != null && restaurantDetailsResults == null) {
            workMatesDetailsViewStateMediatorLiveData.setValue(mapWithoutDetails(nearbySearchResults, usersWithFavoriteRestaurant));
        }else if (nearbySearchResults != null){
            workMatesDetailsViewStateMediatorLiveData.setValue(map(nearbySearchResults,restaurantDetailsResults ,usersWithFavoriteRestaurant));
        }
    }

    // MAP WITHOUT DETAILS, IT CAN HAPPEN WHEN USER HAS NO NETWORK AVAILABLE
    // (ASSUME THAT THE GOOGLE DETAILS SERVICE WILL WORK AS LONG AS NEARBY WORKS)
    private RestaurantDetailsViewState mapWithoutDetails(@NonNull NearbySearchResults nearbySearchResults,
                                                         List<UserWithFavoriteRestaurant> usersWithFavoriteRestaurant) {

        List<String> restaurantAsFavoriteId = new ArrayList<>();

        if (usersWithFavoriteRestaurant != null) {
            for (int i = 0; i < usersWithFavoriteRestaurant.size(); i++) {
                restaurantAsFavoriteId.add(usersWithFavoriteRestaurant.get(i).getRestaurantId());

            }
        }

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
            if (placeId.equals(nearbySearchResults.getResults().get(i).getRestaurantId())) {
                boolean isFavorite = false;
                if(restaurantAsFavoriteId.contains(nearbySearchResults.getResults().get(i).getRestaurantId())){
                    isFavorite = true;
                }
                result = new RestaurantDetailsViewState(
                        nearbySearchResults.getResults().get(i).getRestaurantName(),
                        nearbySearchResults.getResults().get(i).getRestaurantAddress(),
                        photoReference(nearbySearchResults.getResults().get(i).getRestaurantPhotos()),
                        "",
                        "",
                        nearbySearchResults.getResults().get(i).getRestaurantId(),
                        convertRatingStars(nearbySearchResults.getResults().get(i).getRating()),
                        isFavorite

                );
            }
        }
        return result;

    }

    private RestaurantDetailsViewState map(@NonNull NearbySearchResults nearbySearchResults,
                                           @NonNull List<RestaurantDetailsResult> restaurantDetailsResults,
                                           List<UserWithFavoriteRestaurant> usersWithFavoriteRestaurant) {

        List<String> restaurantAsFavoriteId = new ArrayList<>();
        String restaurantPhoneNumber = "";
        String restaurantWebsite = "";

        if (usersWithFavoriteRestaurant != null) {
            for (int i = 0; i < usersWithFavoriteRestaurant.size(); i++) {
                restaurantAsFavoriteId.add(usersWithFavoriteRestaurant.get(i).getRestaurantId());

            }
        }

        for (int i = 0; i < restaurantDetailsResults.size(); i++) {
            if (restaurantDetailsResults.get(i).getDetailsResult().getPlaceId().equals(placeId)) {
                if (restaurantDetailsResults.get(i).getDetailsResult().getFormatted_phone_number() != null) {
                    restaurantPhoneNumber = restaurantDetailsResults.get(i).getDetailsResult().getFormatted_phone_number();

                }
                if (restaurantDetailsResults.get(i).getDetailsResult().getWebsite() != null) {
                    restaurantWebsite = restaurantDetailsResults.get(i).getDetailsResult().getWebsite();;

                }
            }
        }

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
            if (placeId.equals(nearbySearchResults.getResults().get(i).getRestaurantId())) {
                boolean isFavorite = false;
                if(restaurantAsFavoriteId.contains(nearbySearchResults.getResults().get(i).getRestaurantId())){
                    isFavorite = true;
                }
                result = new RestaurantDetailsViewState(
                        nearbySearchResults.getResults().get(i).getRestaurantName(),
                        nearbySearchResults.getResults().get(i).getRestaurantAddress(),
                        photoReference(nearbySearchResults.getResults().get(i).getRestaurantPhotos()),
                        restaurantPhoneNumber,
                        restaurantWebsite,
                        nearbySearchResults.getResults().get(i).getRestaurantId(),
                        convertRatingStars(nearbySearchResults.getResults().get(i).getRating()),
                        isFavorite

                );
            }
        }
        return result;

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

    // GET RESTAURANT TO PARSE HIS DETAILS
    public void init(String placeId){
        this.placeId = placeId;
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
