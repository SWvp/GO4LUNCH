package com.kardabel.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWithFavoriteRestaurant;
import com.kardabel.go4lunch.repository.WorkmatesRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkMatesViewModel extends ViewModel {

    private final MediatorLiveData<List<WorkMatesViewState>> workMatesViewStateMediatorLiveData = new MediatorLiveData<>();

    public WorkMatesViewModel(
            @NonNull WorkmatesRepository workmatesRepository
    ) {


        LiveData<List<UserModel>> workMatesLiveData = workmatesRepository.getWorkmates();
        LiveData<List<UserWithFavoriteRestaurant>> favoriteRestaurantsLiveData = workmatesRepository.getRestaurantsAddAsFavorite();

        // OBSERVERS

        workMatesViewStateMediatorLiveData.addSource(workMatesLiveData, userModels -> combine(userModels, favoriteRestaurantsLiveData.getValue()));
        workMatesViewStateMediatorLiveData.addSource(favoriteRestaurantsLiveData, usersWithRestaurant -> combine(workMatesLiveData.getValue(), usersWithRestaurant));
    }

    private void combine(List<UserModel> users, List<UserWithFavoriteRestaurant> usersWithRestaurant) {

        if (usersWithRestaurant != null) {
            workMatesViewStateMediatorLiveData.setValue(mapWithFavorites(users, usersWithRestaurant));

        } else {
            workMatesViewStateMediatorLiveData.setValue(map(users));

        }
    }

    // TODO: si le workmate c'est moi, ne pas afficher
    private List<WorkMatesViewState> map(List<UserModel> users) {
        List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {

            String description = users.get(i).getUserName();
            String avatar = users.get(i).getAvatarURL();
            String restaurant = "";

            workMatesViewStateList.add(new WorkMatesViewState(
                    description + restaurant,
                    avatar,
                    restaurant,
                    false
            ));
        }
        return workMatesViewStateList;

    }

    private List<WorkMatesViewState> mapWithFavorites(List<UserModel> users,
                                                      List<UserWithFavoriteRestaurant> usersWithRestaurant) {
        List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            String userId = users.get(i).getUid();

            String description = users.get(i).getUserName();
            String avatar = users.get(i).getAvatarURL();
            String restaurant = userChoice(userId, usersWithRestaurant);

            workMatesViewStateList.add(new WorkMatesViewState(
                    description + restaurant,
                    avatar,
                    restaurant,
                    isUserHasDecided(userId, usersWithRestaurant)
            ));
        }
        return workMatesViewStateList;

    }

    private boolean isUserHasDecided(String userId,
                                     List<UserWithFavoriteRestaurant> usersWithRestaurant) {
        boolean state = false;

        for (int i = 0; i < usersWithRestaurant.size(); i++) {
            if (usersWithRestaurant.get(i).getUserId().equals(userId)) {
                state = true;

            }
        }
        return state;

    }

    private String userChoice(@NonNull String userId,
                              @NonNull List<UserWithFavoriteRestaurant> usersWithRestaurant) {
        String restaurantName = " hasn't decided yet";

        for (int i = 0; i < usersWithRestaurant.size(); i++) {
            if (usersWithRestaurant.get(i).getUserId().equals(userId)) {
                restaurantName = " (" + usersWithRestaurant.get(i).getRestaurantName() + ")";

            }
        }
        return restaurantName;

    }


    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<List<WorkMatesViewState>> getWorkmatesViewStateLiveData() {
        return workMatesViewStateMediatorLiveData;

    }

}