package com.kardabel.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.WorkmateWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.WorkmatesWhoMadeRestaurantChoiceRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkMatesViewModel extends ViewModel {

    private final MediatorLiveData<List<WorkMatesViewState>> workMatesViewStateMediatorLiveData = new MediatorLiveData<>();

    public WorkMatesViewModel(
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull WorkmatesWhoMadeRestaurantChoiceRepository workmatesWhoMadeRestaurantChoiceRepository
    ) {


        LiveData<List<UserModel>> workMatesLiveData = workmatesRepository.getWorkmates();
        LiveData<List<WorkmateWhoMadeRestaurantChoice>> workmatesWhoMadeChoiceLiveData = workmatesWhoMadeRestaurantChoiceRepository.getWorkmatesWhoMadeRestaurantChoice();

        // OBSERVERS

        workMatesViewStateMediatorLiveData.addSource(workMatesLiveData, userModels -> combine(userModels, workmatesWhoMadeChoiceLiveData.getValue()));
        workMatesViewStateMediatorLiveData.addSource(workmatesWhoMadeChoiceLiveData, usersWithRestaurant -> combine(workMatesLiveData.getValue(), usersWithRestaurant));
    }

    private void combine(List<UserModel> users, List<WorkmateWhoMadeRestaurantChoice> usersWithRestaurant) {

        if (usersWithRestaurant != null) {
            workMatesViewStateMediatorLiveData.setValue(mapWorkmateWhoChose(users, usersWithRestaurant));

        }
//     else {
//         workMatesViewStateMediatorLiveData.setValue(map(users));

//     }
    }

 // private List<WorkMatesViewState> map(List<UserModel> users) {
 //     List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

 //     for (int i = 0; i < users.size(); i++) {

 //         String workmateName = users.get(i).getUserName();
 //         String avatar = users.get(i).getAvatarURL();
 //         String workmateId = users.get(i).getUid();
 //         String restaurant = "";

 //         workMatesViewStateList.add(new WorkMatesViewState(
 //                 workmateName,
 //                 workmateName + restaurant,
 //                 avatar,
 //                 restaurant,
 //                 workmateId,
 //                 false
 //         ));
 //     }
 //     return workMatesViewStateList;

 // }

    private List<WorkMatesViewState> mapWorkmateWhoChose(List<UserModel> users,
                                                         List<WorkmateWhoMadeRestaurantChoice> usersWithRestaurant) {
        List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            String userId = users.get(i).getUid();

            String workmateName = users.get(i).getUserName();
            String avatar = users.get(i).getAvatarURL();
            String workmateId = users.get(i).getUid();
            String restaurant = userChoice(userId, usersWithRestaurant);

            workMatesViewStateList.add(new WorkMatesViewState(
                    workmateName,
                    workmateName + restaurant,
                    avatar,
                    restaurant,
                    workmateId,
                    isUserHasDecided(userId, usersWithRestaurant)
            ));
        }
        return workMatesViewStateList;

    }

    private boolean isUserHasDecided(String userId,
                                     List<WorkmateWhoMadeRestaurantChoice> usersWithRestaurant) {
        boolean state = false;

        for (int i = 0; i < usersWithRestaurant.size(); i++) {
            if (usersWithRestaurant.get(i).getUserId().equals(userId)) {
                state = true;

            }
        }
        return state;

    }

    private String userChoice(@NonNull String userId,
                              @NonNull List<WorkmateWhoMadeRestaurantChoice> usersWithRestaurant) {
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