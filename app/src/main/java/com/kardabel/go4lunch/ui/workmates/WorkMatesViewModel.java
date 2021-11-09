package com.kardabel.go4lunch.ui.workmates;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class WorkMatesViewModel extends ViewModel {

    private final MediatorLiveData<List<WorkMatesViewState>> workMatesViewStateMediatorLiveData = new MediatorLiveData<>();

    public WorkMatesViewModel(
            @NonNull Application application,
            @NonNull LocationRepository locationRepository,
            @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase,
            @NonNull RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase,
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull Clock clock
    ) {


        //LiveData<NearbySearchResults> nearbySearchResultsLiveData = nearbySearchResultsUseCase.getNearbySearchResultsLiveData();
        LiveData<List<UserModel>> workMatesLiveData = workmatesRepository.getWorkmates();
        LiveData<List<RestaurantSearch>> restaurantsSearchLiveData = workmatesRepository.getRestaurantsWithFavorite();

        // OBSERVERS

        workMatesViewStateMediatorLiveData.addSource(workMatesLiveData, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                combine(userModels, restaurantsSearchLiveData.getValue());

            }
        });

        workMatesViewStateMediatorLiveData.addSource(restaurantsSearchLiveData, new Observer<List<RestaurantSearch>>() {
            @Override
            public void onChanged(List<RestaurantSearch> restaurants) {
                combine(workMatesLiveData.getValue(), restaurants);


            }
        });
    }

    private void combine(List<UserModel> users, List<RestaurantSearch> restaurants) {

        if(restaurants != null){
            workMatesViewStateMediatorLiveData.setValue(mapWithFavorites(users, restaurants));

        }else{
            workMatesViewStateMediatorLiveData.setValue(map(users));

        }
    }

    private List<WorkMatesViewState> map(List<UserModel> users) {
        List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < users.size() ; i++) {

            String description = users.get(i).getUserName();
            String avatar = users.get(i).getAvatarURL();
            String restaurant = " (test)";

            workMatesViewStateList.add(new WorkMatesViewState(
                    description + restaurant,
                    avatar,
                    restaurant
            ));

        }

        return workMatesViewStateList;

    }

    private List<WorkMatesViewState> mapWithFavorites(List<UserModel> users, List<RestaurantSearch> restaurants) {
        List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < users.size() ; i++) {


            String description = users.get(i).getUserName();
            String avatar = users.get(i).getAvatarURL();
            String restaurant = " (test)";

            workMatesViewStateList.add(new WorkMatesViewState(
                    description + restaurant,
                    avatar,
                    restaurant
            ));

        }

        return workMatesViewStateList;

    }


    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<List<WorkMatesViewState>> getWorkmatesViewStateLiveData() {
        return workMatesViewStateMediatorLiveData;

    }
}