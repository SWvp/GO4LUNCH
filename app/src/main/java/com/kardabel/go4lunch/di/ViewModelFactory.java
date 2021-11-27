package com.kardabel.go4lunch.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.MainActivityViewModel;
import com.kardabel.go4lunch.MainApplication;
import com.kardabel.go4lunch.repository.AutocompleteRepository;
import com.kardabel.go4lunch.repository.FavoriteRestaurantsRepository;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;
import com.kardabel.go4lunch.repository.RestaurantDetailsResponseRepository;
import com.kardabel.go4lunch.repository.UsersSearchRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.WorkmatesWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewModel;
import com.kardabel.go4lunch.ui.restaurants.RestaurantsViewModel;
import com.kardabel.go4lunch.ui.workmates.WorkMatesViewModel;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsByIdUseCase;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.GetPredictionsUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsByIdUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsUseCase;

import java.time.Clock;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;
    private final Application application;

    private final LocationRepository locationRepository;
    private final WorkmatesRepository workmatesRepository;
    private final UsersSearchRepository usersSearchRepository;
    private final FavoriteRestaurantsRepository favoriteRestaurantsRepository;
    private final WorkmatesWhoMadeRestaurantChoiceRepository workmatesWhoMadeRestaurantChoiceRepository;

    private final GetNearbySearchResultsUseCase getNearbySearchResultsUseCase;
    private final GetNearbySearchResultsByIdUseCase getNearbySearchResultsByIdUseCase;
    private final GetRestaurantDetailsResultsUseCase getRestaurantDetailsResultsUseCase;
    private final GetRestaurantDetailsResultsByIdUseCase getRestaurantDetailsResultsByIdUseCase;
    private final GetPredictionsUseCase getPredictionsUseCase;

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
        NearbySearchResponseRepository nearbySearchResponseRepository = new NearbySearchResponseRepository(googleMapsApi);
        RestaurantDetailsResponseRepository restaurantDetailsResponseRepository = new RestaurantDetailsResponseRepository(googleMapsApi);
        AutocompleteRepository autocompleteRepository = new AutocompleteRepository(googleMapsApi);

        this.application = MainApplication.getApplication();
        this.locationRepository = new LocationRepository();
        this.workmatesRepository = new WorkmatesRepository();
        this.usersSearchRepository = new UsersSearchRepository();
        this.favoriteRestaurantsRepository = new FavoriteRestaurantsRepository();
        this.workmatesWhoMadeRestaurantChoiceRepository = new WorkmatesWhoMadeRestaurantChoiceRepository();

        this.getNearbySearchResultsUseCase = new GetNearbySearchResultsUseCase(
                locationRepository,
                nearbySearchResponseRepository);
        this.getNearbySearchResultsByIdUseCase = new GetNearbySearchResultsByIdUseCase(
                locationRepository,
                nearbySearchResponseRepository);
        this.getRestaurantDetailsResultsUseCase = new GetRestaurantDetailsResultsUseCase(
                locationRepository,
                nearbySearchResponseRepository,
                restaurantDetailsResponseRepository);
        this.getRestaurantDetailsResultsByIdUseCase = new GetRestaurantDetailsResultsByIdUseCase(
                restaurantDetailsResponseRepository
        );
        this.getPredictionsUseCase = new GetPredictionsUseCase(
                locationRepository,
                autocompleteRepository);
        //this.getUsersSearchUseCase = new GetUsersSearchUseCase(usersSearchRepository);
    }


    // CREATE INSTANCE FOR EACH VIEWMODEL
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantsViewModel.class)) {
            return (T) new RestaurantsViewModel(
                    application,
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    getRestaurantDetailsResultsUseCase,
                    workmatesWhoMadeRestaurantChoiceRepository,
                    usersSearchRepository,
                    Clock.systemDefaultZone());
        } else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    workmatesWhoMadeRestaurantChoiceRepository,
                    usersSearchRepository);
        } else if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(
                    application,
                    locationRepository,
                    workmatesRepository,
                    getPredictionsUseCase,
                    usersSearchRepository,
                    workmatesWhoMadeRestaurantChoiceRepository);
        } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)) {
            return (T) new RestaurantDetailsViewModel(
                    getNearbySearchResultsByIdUseCase,
                    getRestaurantDetailsResultsByIdUseCase,
                    workmatesWhoMadeRestaurantChoiceRepository,
                    workmatesRepository,
                    favoriteRestaurantsRepository);
        } else if (modelClass.isAssignableFrom(WorkMatesViewModel.class)) {
            return (T) new WorkMatesViewModel(
                    workmatesRepository,
                    workmatesWhoMadeRestaurantChoiceRepository
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
