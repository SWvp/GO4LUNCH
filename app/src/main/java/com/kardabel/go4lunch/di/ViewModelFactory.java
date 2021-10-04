package com.kardabel.go4lunch.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.MainApplication;
import com.kardabel.go4lunch.MainActivityViewModel;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.PlaceDetailsResponseRepository;
import com.kardabel.go4lunch.repository.PlaceSearchResponseRepository;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsViewModel;
import com.kardabel.go4lunch.ui.listview.ListViewViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewViewModel;
import com.kardabel.go4lunch.usecase.PlaceDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.PlaceSearchResultsUseCase;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;
    private final Application application;
    private final LocationRepository locationRepository;
    private final PlaceSearchResponseRepository placeSearchResponseRepository;
    private final PlaceDetailsResponseRepository placeDetailsResponseRepository;
    private final PlaceSearchResultsUseCase placeSearchResultsUseCase;
    private final PlaceDetailsResultsUseCase placeDetailsResultsUseCase;

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
        this.placeSearchResponseRepository = new PlaceSearchResponseRepository(googleMapsApi);
        this.placeDetailsResponseRepository = new PlaceDetailsResponseRepository(googleMapsApi);
        this.placeSearchResultsUseCase = new PlaceSearchResultsUseCase(locationRepository, placeSearchResponseRepository);
        this.placeDetailsResultsUseCase = new PlaceDetailsResultsUseCase(locationRepository, placeSearchResponseRepository, placeDetailsResponseRepository);

    }


    // CREATE INSTANCE FOR EACH VIEWMODEL
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ListViewViewModel.class)) {
            return (T) new ListViewViewModel(locationRepository, placeSearchResultsUseCase, placeDetailsResultsUseCase);
        } else if (modelClass.isAssignableFrom(MapViewViewModel.class)) {
            return (T) new MapViewViewModel(locationRepository, placeSearchResultsUseCase);
        } else if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(application, locationRepository);
        } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)) {
            return (T) new RestaurantDetailsViewModel(placeSearchResultsUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
