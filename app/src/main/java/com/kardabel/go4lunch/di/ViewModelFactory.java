package com.kardabel.go4lunch.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.MainApplication;
import com.kardabel.go4lunch.MainActivityViewModel;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.PlaceDetailsResponseRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsViewModel;
import com.kardabel.go4lunch.ui.listview.RestaurantsViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewModel;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.util.CurrentConvertedHour;
import com.kardabel.go4lunch.util.CurrentNumericDay;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;
    private final Application application;
    private final LocationRepository locationRepository;
    private final NearbySearchResponseRepository nearbySearchResponseRepository;
    private final PlaceDetailsResponseRepository placeDetailsResponseRepository;
    private final NearbySearchResultsUseCase nearbySearchResultsUseCase;
    private final RestaurantDetailsResultsUseCase mRestaurantDetailsResultsUseCase;
    private final CurrentNumericDay currentNumericDay;
    private final CurrentConvertedHour currentConvertedHour;

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
        this.placeDetailsResponseRepository = new PlaceDetailsResponseRepository(googleMapsApi);
        this.nearbySearchResultsUseCase = new NearbySearchResultsUseCase(locationRepository, nearbySearchResponseRepository);
        this.mRestaurantDetailsResultsUseCase = new RestaurantDetailsResultsUseCase(locationRepository, nearbySearchResponseRepository, placeDetailsResponseRepository);
        this.currentNumericDay = new CurrentNumericDay();
        this.currentConvertedHour = new CurrentConvertedHour();

    }


    // CREATE INSTANCE FOR EACH VIEWMODEL
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantsViewModel.class)) {
            return (T) new RestaurantsViewModel(locationRepository, nearbySearchResultsUseCase, mRestaurantDetailsResultsUseCase, currentNumericDay, currentConvertedHour);
        } else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(locationRepository, nearbySearchResultsUseCase);
        } else if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(application, locationRepository);
        } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)) {
            return (T) new RestaurantDetailsViewModel(nearbySearchResultsUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
