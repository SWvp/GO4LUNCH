package com.kardabel.go4lunch.ui.workmates;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.ui.restaurants.RestaurantsWrapperViewState;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;

import java.time.Clock;
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
            @NonNull Clock clock
    ) {

        this.clock = clock;
        this.application = application;



        LiveData<NearbySearchResults> nearbySearchResultsLiveData = nearbySearchResultsUseCase.getNearbySearchResultsLiveData();


        // OBSERVERS




    }




    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<List<WorkMatesViewState>> getWorkmatesViewStateLiveData() {
        return workMatesViewStateMediatorLiveData;

    }
}