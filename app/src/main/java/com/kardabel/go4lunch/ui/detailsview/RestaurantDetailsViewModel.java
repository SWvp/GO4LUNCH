package com.kardabel.go4lunch.ui.detailsview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.pojo.FavoriteRestaurant;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.FavoriteRestaurantsRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.usecase.ClickOnChoseRestaurantButtonUseCase;
import com.kardabel.go4lunch.usecase.ClickOnFavoriteRestaurantUseCase;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsByIdUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsByIdUseCase;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsViewModel extends ViewModel {

    private RestaurantDetailsViewState result;
    @NonNull
    private final Application application;
    @NonNull
    private final GetNearbySearchResultsByIdUseCase getNearbySearchResultsByIdUseCase;
    @NonNull
    private final GetRestaurantDetailsResultsByIdUseCase getRestaurantDetailsResultsByIdUseCase;
    @NonNull
    private final WorkmatesRepository workmatesRepository;
    @NonNull
    private final FavoriteRestaurantsRepository favoriteRestaurantsRepository;
    @NonNull
    private final UsersWhoMadeRestaurantChoiceRepository mUsersWhoMadeRestaurantChoiceRepository;
    @NonNull
    private final GetCurrentUserIdUseCase getCurrentUserIdUseCase;

    private final MediatorLiveData<RestaurantDetailsViewState> workMatesDetailsViewStateMediatorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<RestaurantDetailsWorkmatesViewState>> workmatesLikeThisRestaurantMediatorLiveData = new MediatorLiveData<>();

    public RestaurantDetailsViewModel(@NonNull Application application,
                                      @NonNull GetNearbySearchResultsByIdUseCase getNearbySearchResultsByIdUseCase,
                                      @NonNull GetRestaurantDetailsResultsByIdUseCase getRestaurantDetailsResultsByIdUseCase,
                                      @NonNull UsersWhoMadeRestaurantChoiceRepository usersWhoMadeRestaurantChoiceRepository,
                                      @NonNull WorkmatesRepository workmatesRepository,
                                      @NonNull FavoriteRestaurantsRepository favoriteRestaurantsRepository,
                                      @NonNull GetCurrentUserIdUseCase getCurrentUserIdUseCase) {

        this.application = application;
        this.getNearbySearchResultsByIdUseCase = getNearbySearchResultsByIdUseCase;
        this.getRestaurantDetailsResultsByIdUseCase = getRestaurantDetailsResultsByIdUseCase;
        this.mUsersWhoMadeRestaurantChoiceRepository = usersWhoMadeRestaurantChoiceRepository;
        this.workmatesRepository = workmatesRepository;
        this.favoriteRestaurantsRepository = favoriteRestaurantsRepository;
        this.getCurrentUserIdUseCase = getCurrentUserIdUseCase;

    }

    public void init(String placeId) {
        LiveData<RestaurantSearch> restaurantLiveData =
                getNearbySearchResultsByIdUseCase.invoke(placeId);
        LiveData<RestaurantDetailsResult> restaurantDetailsLiveData =
                getRestaurantDetailsResultsByIdUseCase.invoke(placeId);
        LiveData<List<UserWhoMadeRestaurantChoice>> workmatesWhoMadeRestaurantChoiceLiveData =
                mUsersWhoMadeRestaurantChoiceRepository.getWorkmatesWhoMadeRestaurantChoice();
        LiveData<List<UserModel>> workMatesLiveData =
                workmatesRepository.getWorkmates();
        LiveData<List<FavoriteRestaurant>> favoriteRestaurantsLiveData =
                favoriteRestaurantsRepository.getFavoriteRestaurants();


        // OBSERVERS FOR RESTAURANT DETAILS

        workMatesDetailsViewStateMediatorLiveData.addSource(restaurantLiveData, restaurant -> combine(
                restaurant,
                workmatesWhoMadeRestaurantChoiceLiveData.getValue(),
                restaurantDetailsLiveData.getValue(),
                favoriteRestaurantsLiveData.getValue()));

        workMatesDetailsViewStateMediatorLiveData.addSource(workmatesWhoMadeRestaurantChoiceLiveData, workmatesWithFavoriteRestaurant -> combine(
                restaurantLiveData.getValue(),
                workmatesWithFavoriteRestaurant,
                restaurantDetailsLiveData.getValue(),
                favoriteRestaurantsLiveData.getValue()));

        workMatesDetailsViewStateMediatorLiveData.addSource(restaurantDetailsLiveData, restaurantDetailsResults -> combine(
                restaurantLiveData.getValue(),
                workmatesWhoMadeRestaurantChoiceLiveData.getValue(),
                restaurantDetailsResults,
                favoriteRestaurantsLiveData.getValue()));

        workMatesDetailsViewStateMediatorLiveData.addSource(favoriteRestaurantsLiveData, favoriteRestaurants -> combine(
                restaurantLiveData.getValue(),
                workmatesWhoMadeRestaurantChoiceLiveData.getValue(),
                restaurantDetailsLiveData.getValue(),
                favoriteRestaurants));


        // OBSERVERS FOR WORKMATES RECYCLERVIEW

        workmatesLikeThisRestaurantMediatorLiveData.addSource(restaurantLiveData, restaurantSearch -> combineWorkmates(restaurantSearch, workmatesWhoMadeRestaurantChoiceLiveData.getValue(), workMatesLiveData.getValue()));
        workmatesLikeThisRestaurantMediatorLiveData.addSource(workmatesWhoMadeRestaurantChoiceLiveData, userWithFavoriteRestaurants -> combineWorkmates(
                restaurantLiveData.getValue(),
                userWithFavoriteRestaurants,
                workMatesLiveData.getValue()));

        workmatesLikeThisRestaurantMediatorLiveData.addSource(workMatesLiveData, userModels -> RestaurantDetailsViewModel.this.combineWorkmates(
                restaurantLiveData.getValue(),
                workmatesWhoMadeRestaurantChoiceLiveData.getValue(),
                userModels));

    }

    // COMBINE FIRST MEDIATOR FOR RESTAURANT DETAILS
    private void combine(@Nullable RestaurantSearch restaurant,
                         @Nullable List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices,
                         @Nullable RestaurantDetailsResult restaurantDetails,
                         @Nullable List<FavoriteRestaurant> favoriteRestaurants) {
        if (restaurant != null && restaurantDetails == null) {
            workMatesDetailsViewStateMediatorLiveData.setValue(mapWithoutDetails(
                    restaurant,
                    userWhoMadeRestaurantChoices,
                    favoriteRestaurants));
        } else if (restaurant != null && favoriteRestaurants != null) {
            workMatesDetailsViewStateMediatorLiveData.setValue(map(
                    restaurant,
                    userWhoMadeRestaurantChoices,
                    restaurantDetails,
                    favoriteRestaurants));
        }
    }

    // MAP WITHOUT DETAILS, WHEN USER HAS NO LONGER NETWORK AVAILABLE
    // (ASSUME THAT THE GOOGLE DETAILS SERVICE WILL WORK AS LONG AS NEARBY WORKS)
    private RestaurantDetailsViewState mapWithoutDetails(@NonNull RestaurantSearch restaurant,
                                                         List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices,
                                                         List<FavoriteRestaurant> favoriteRestaurants) {

        String userId = getCurrentUserIdUseCase.invoke();

        // CHECK IF THE RESTAURANT IS USER CHOICE
        boolean isMyRestaurant = false;
        if (userWhoMadeRestaurantChoices != null) {
            for (int i = 0; i < userWhoMadeRestaurantChoices.size(); i++) {

                if (userWhoMadeRestaurantChoices.get(i).getRestaurantId().equals(restaurant.getRestaurantId())) {
                    if (userWhoMadeRestaurantChoices.get(i).getUserId().equals(userId)) {
                        isMyRestaurant = true;
                    }
                }
            }
        }
        int restaurantChoiceState = R.drawable.hasnt_decided;
        if (isMyRestaurant) {
            restaurantChoiceState = R.drawable.has_decided;
        }

        // CHECK IF THIS RESTAURANT IS IN USERS FAVORITE
        int detailLikeButton = R.drawable.detail_favorite_star_empty;
        for (int i = 0; i < favoriteRestaurants.size(); i++) {
            if (favoriteRestaurants.get(i).getRestaurantId().equals(restaurant.getRestaurantId())) {
                detailLikeButton = R.drawable.details_favorite_star_full;

            }
        }

        result = new RestaurantDetailsViewState(
                restaurant.getRestaurantName(),
                restaurant.getRestaurantAddress(),
                application.getString(R.string.api_url)
                        + application.getString(R.string.photo_reference)
                        + photoReference(restaurant.getRestaurantPhotos())
                        + application.getString(R.string.and_key)
                        + application.getString(R.string.google_map_key),
                "",
                "",
                restaurant.getRestaurantId(),
                convertRatingStars(restaurant.getRating()),
                restaurantChoiceState,
                detailLikeButton

        );
        return result;

    }

    private RestaurantDetailsViewState map(
            RestaurantSearch restaurant,
            List<UserWhoMadeRestaurantChoice>
                    userWhoMadeRestaurantChoices,
            RestaurantDetailsResult restaurantDetails,
            List<FavoriteRestaurant> favoriteRestaurants) {

        String userId = getCurrentUserIdUseCase.invoke();

        String restaurantPhoneNumber;
        String restaurantWebsite;

        // CHECK IF THE RESTAURANT IS USER CHOICE
        boolean isMyRestaurant = false;
        if (userWhoMadeRestaurantChoices != null) {
            for (int i = 0; i < userWhoMadeRestaurantChoices.size(); i++) {

                if (userWhoMadeRestaurantChoices.get(i).getRestaurantId().equals(restaurant.getRestaurantId())) {
                    if (userWhoMadeRestaurantChoices.get(i).getUserId().equals(userId)) {
                        isMyRestaurant = true;
                    }
                }
            }
        }

        int restaurantChoiceState = R.drawable.hasnt_decided;
        if (isMyRestaurant) {
            restaurantChoiceState = R.drawable.has_decided;
        }

        // CHECK IF PHONE NUMBER IS AVAILABLE
        if (restaurantDetails.getDetailsResult().getFormattedPhoneNumber() != null) {
            restaurantPhoneNumber = restaurantDetails.getDetailsResult().getFormattedPhoneNumber();
        } else {
            restaurantPhoneNumber = "no phone number";
        }

        // CHECK IF WEBSITE ADDRESS IS AVAILABLE
        if (restaurantDetails.getDetailsResult().getWebsite() != null) {
            restaurantWebsite = restaurantDetails.getDetailsResult().getWebsite();
        } else {
            restaurantWebsite = "https://www.google.com/";
        }

        // CHECK IF THIS RESTAURANT IS IN USERS FAVORITE
        int detailLikeButton = R.drawable.detail_favorite_star_empty;
        for (int i = 0; i < favoriteRestaurants.size(); i++) {
            if (favoriteRestaurants.get(i).getRestaurantId().equals(restaurant.getRestaurantId())) {
                detailLikeButton = R.drawable.details_favorite_star_full;

            }
        }

        result = new RestaurantDetailsViewState(
                restaurant.getRestaurantName(),
                restaurant.getRestaurantAddress(),
                application.getString(R.string.api_url)
                        + application.getString(R.string.photo_reference)
                        + photoReference(restaurant.getRestaurantPhotos())
                        + application.getString(R.string.and_key)
                        + application.getString(R.string.google_map_key),
                restaurantPhoneNumber,
                restaurantWebsite,
                restaurant.getRestaurantId(),
                convertRatingStars(restaurant.getRating()),
                restaurantChoiceState,
                detailLikeButton

        );
        return result;

    }

    // COMBINE SECOND MEDIATOR FOR WORKMATES WITH THIS RESTAURANT IN FAVORITE
    private void combineWorkmates(@Nullable RestaurantSearch restaurant,
                                  @Nullable List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices,
                                  @Nullable List<UserModel> users) {

        if (userWhoMadeRestaurantChoices != null && users != null && restaurant != null) {
            workmatesLikeThisRestaurantMediatorLiveData.setValue(mapWorkmates(
                    restaurant,
                    userWhoMadeRestaurantChoices,
                    users));
        }
    }

    // MAP THE RECYCLER VIEW ITEMS FOR WORKMATES WHO HAVE THIS ITEM IN FAVORITE
    private List<RestaurantDetailsWorkmatesViewState> mapWorkmates(RestaurantSearch restaurant,
                                                                   List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices,
                                                                   List<UserModel> users) {

        List<RestaurantDetailsWorkmatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < userWhoMadeRestaurantChoices.size(); i++) {
            if (userWhoMadeRestaurantChoices.get(i).getRestaurantId().equals(restaurant.getRestaurantId())) {
                for (int j = 0; j < users.size(); j++) {
                    if (users.get(j).getUid().equals(userWhoMadeRestaurantChoices.get(i).getUserId())) {
                        String name = users.get(j).getUserName() + " is joining!";
                        String avatar = users.get(j).getAvatarURL();

                        workMatesViewStateList.add(new RestaurantDetailsWorkmatesViewState(
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
        return application.getString(R.string.photo_unavailable);

    }

    private double convertRatingStars(double rating) {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return Math.round(rating * 3 / 5);

    }

    // CLICK ON THE CHOSE FAB
    public void onChoseRestaurantButtonClick(String restaurantId, String restaurantName) {
        ClickOnChoseRestaurantButtonUseCase.onRestaurantSelectedClick(restaurantId, restaurantName);

    }

    // CLICK ON THE FAVORITE ICON
    public void onFavoriteIconClick(String restaurantId, String restaurantName) {
        ClickOnFavoriteRestaurantUseCase.onFavoriteRestaurantClick(restaurantId, restaurantName);

    }

    public LiveData<RestaurantDetailsViewState> getRestaurantDetailsViewStateLiveData() {
        return workMatesDetailsViewStateMediatorLiveData;

    }

    public LiveData<List<RestaurantDetailsWorkmatesViewState>> getWorkmatesLikeThisRestaurant() {
        return workmatesLikeThisRestaurantMediatorLiveData;

    }
}
