package com.kardabel.go4lunch.ui.workmates;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.ui.restaurants.RestaurantsWrapperViewState;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.WorkmatesResultsUseCase;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class WorkMatesViewModel extends ViewModel {

    private final MediatorLiveData<List<WorkMatesViewState>> workMatesViewStateMediatorLiveData = new MediatorLiveData<>();

    private final Application application;
    private final Clock clock;

    public WorkMatesViewModel(
            @NonNull Application application,
            @NonNull LocationRepository locationRepository,
            @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase,
            @NonNull RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase,
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull Clock clock
    ) {

        this.clock = clock;
        this.application = application;





        LiveData<NearbySearchResults> nearbySearchResultsLiveData = nearbySearchResultsUseCase.getNearbySearchResultsLiveData();
        LiveData<List<UserModel>> workMatesLiveData = workmatesRepository.getWorkmates();


        // OBSERVERS

        workMatesViewStateMediatorLiveData.addSource(workMatesLiveData, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                combine(userModels);

            }
        });




    }

    private void combine(List<UserModel> userModels) {

        workMatesViewStateMediatorLiveData.setValue(map(userModels));

    }

    private List<WorkMatesViewState> map(List<UserModel> userModels) {

        List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < userModels.size() ; i++) {


            String description = userModels.get(i).getUserName();
            String avatar = userModels.get(i).getAvatarURL();
            String restaurant = "coucou";

            workMatesViewStateList.add(new WorkMatesViewState(
                    description,
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