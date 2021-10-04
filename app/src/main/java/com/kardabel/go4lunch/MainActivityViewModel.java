package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.PlaceSearchResultsUseCase;
import com.kardabel.go4lunch.util.SingleLiveEvent;

public class MainActivityViewModel extends ViewModel {

    private SingleLiveEvent<PermissionsViewAction> actionSingleLiveEvent = new SingleLiveEvent<>();

    private LocationRepository locationRepository;
    private Application application;
    private MainActivity mainActivity;

    public MainActivityViewModel(@Nullable Application application, @Nullable LocationRepository locationRepository) {
        super();
        this.application = application;
        this.locationRepository = locationRepository;

    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(
                application, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_GRANTED);
            permissionGranted();

        } //else if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, ACCESS_FINE_LOCATION)) {

         // In an educational UI, explain to the user why your app requires this
         // permission for a specific feature to behave as expected. In this UI,
         // include a "cancel" or "no thanks" button that allows the user to
         // continue using your app without granting the permission.
         // showInContextUI(...);
         else {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_ASKED);

        }
    }

    private void permissionGranted() {
        locationRepository.StartLocationRequest();

    }

    public SingleLiveEvent<PermissionsViewAction> getActionSingleLiveEvent() {
        return actionSingleLiveEvent;

    }
}
