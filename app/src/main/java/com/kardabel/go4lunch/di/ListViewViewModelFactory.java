package com.kardabel.go4lunch.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.MainApplication;
import com.kardabel.go4lunch.UserActivityViewModel;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbyResponseRepository;
import com.kardabel.go4lunch.ui.listview.ListViewViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewViewModel;

public class ListViewViewModelFactory implements ViewModelProvider.Factory {

        private static ListViewViewModelFactory factory;
        private final Application application;
        private final LocationRepository locationRepository;
        private final NearbyResponseRepository nearbyResponseRepository;

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
            this.locationRepository = new LocationRepository();
            this.nearbyResponseRepository = new NearbyResponseRepository();

        }


        // CREATE INSTANCE FOR EACH VIEWMODEL
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ListViewViewModel.class)) {
                return (T) new ListViewViewModel(application, locationRepository, nearbyResponseRepository);

            } else if (modelClass.isAssignableFrom(MapViewViewModel.class)) {
                return (T) new MapViewViewModel(application, locationRepository, nearbyResponseRepository);
            } else if (modelClass.isAssignableFrom(UserActivityViewModel.class)) {
                return (T) new UserActivityViewModel(application, locationRepository);
            }

            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
