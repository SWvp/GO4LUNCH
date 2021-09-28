package com.kardabel.go4lunch.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.MainApplication;
import com.kardabel.go4lunch.MainActivityViewModel;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsViewModel;
import com.kardabel.go4lunch.ui.listview.ListViewViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewViewModel;
import com.kardabel.go4lunch.usecase.NearbyResultsUseCase;

public class ListViewViewModelFactory implements ViewModelProvider.Factory {

        private static ListViewViewModelFactory factory;
        private final Application application;
        //private final LocationRepository locationRepository;
        //private final NearbyResponseRepository nearbyResponseRepository;
        private final NearbyResultsUseCase nearbyResultsUseCase;

        public static ListViewViewModelFactory getInstance() {
            if (factory == null) {
                synchronized (ListViewViewModelFactory.class) {
                    if (factory == null) {
                        factory = new ListViewViewModelFactory();

                    }
                }
            }
            return factory;

        }

        public ListViewViewModelFactory() {
            this.application = MainApplication.getApplication();
            //this.locationRepository = new LocationRepository();
            //this.nearbyResponseRepository = new NearbyResponseRepository();
            this.nearbyResultsUseCase = new NearbyResultsUseCase();

        }


        // CREATE INSTANCE FOR EACH VIEWMODEL
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ListViewViewModel.class)) {
                return (T) new ListViewViewModel(nearbyResultsUseCase);

            } else if (modelClass.isAssignableFrom(MapViewViewModel.class)) {
                return (T) new MapViewViewModel(nearbyResultsUseCase);
            } else if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
                return (T) new MainActivityViewModel(application,nearbyResultsUseCase);
            } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)) {
                return (T) new RestaurantDetailsViewModel(nearbyResultsUseCase);
            }

            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
