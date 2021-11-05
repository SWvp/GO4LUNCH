package com.kardabel.go4lunch.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.MainApplication;
import com.kardabel.go4lunch.MainActivityViewModel;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.RestaurantDetailsResponseRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsViewModel;
import com.kardabel.go4lunch.ui.restaurants.RestaurantsViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewModel;
import com.kardabel.go4lunch.ui.workmates.WorkMatesViewModel;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.WorkmatesResultsUseCase;

import java.time.Clock;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;
    private final Application application;

    private final LocationRepository locationRepository;
    private final NearbySearchResponseRepository nearbySearchResponseRepository;
    private final RestaurantDetailsResponseRepository mRestaurantDetailsResponseRepository;
    private final WorkmatesRepository workmatesRepository;

    private final NearbySearchResultsUseCase nearbySearchResultsUseCase;
    private final RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase;
    private final WorkmatesResultsUseCase workmatesResultsUseCase;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();

                }
            }
        }
        return factory;

    }

    public ViewModelFactory() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GoogleMapsApi googleMapsApi = retrofit.create(GoogleMapsApi.class);
        this.application = MainApplication.getApplication();

        this.locationRepository = new LocationRepository();
        this.nearbySearchResponseRepository = new NearbySearchResponseRepository(googleMapsApi);
        this.mRestaurantDetailsResponseRepository = new RestaurantDetailsResponseRepository(googleMapsApi);
        this.workmatesRepository = new WorkmatesRepository();

        this.nearbySearchResultsUseCase = new NearbySearchResultsUseCase(
                locationRepository,
                nearbySearchResponseRepository);
        this.restaurantDetailsResultsUseCase = new RestaurantDetailsResultsUseCase(
                locationRepository,
                nearbySearchResponseRepository,
                mRestaurantDetailsResponseRepository);
        this.workmatesResultsUseCase = new WorkmatesResultsUseCase(
                locationRepository,
                workmatesRepository);

    }


    // CREATE INSTANCE FOR EACH VIEWMODEL
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantsViewModel.class)) {
            return (T) new RestaurantsViewModel(
                    application,
                    locationRepository,
                    nearbySearchResultsUseCase,
                    restaurantDetailsResultsUseCase,
                    Clock.systemDefaultZone());
        } else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(
                    locationRepository,
                    nearbySearchResultsUseCase);
        } else if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(
                    application,
                    locationRepository,
                    workmatesRepository);
        } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)) {
            return (T) new RestaurantDetailsViewModel(
                    nearbySearchResultsUseCase);
        } else if (modelClass.isAssignableFrom(WorkMatesViewModel.class)) {
            return (T) new WorkMatesViewModel(
                    application,
                    locationRepository,
                    nearbySearchResultsUseCase,
                    restaurantDetailsResultsUseCase,
                    workmatesRepository,
                    Clock.systemDefaultZone());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
