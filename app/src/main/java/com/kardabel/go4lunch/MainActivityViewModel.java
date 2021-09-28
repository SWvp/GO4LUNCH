package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.NearbyResultsUseCase;
import com.kardabel.go4lunch.util.SingleLiveEvent;

public class MainActivityViewModel extends ViewModel {

    private static final int LOCATION_PERMISSION_CODE = 100;

    private SingleLiveEvent<PermissionsViewAction> actionSingleLiveEvent = new SingleLiveEvent<>();

 //   private LocationRepository locationRepository;


//  public UserActivityViewModel(@NonNull Application application, @Nullable LocationRepository locationRepository) {
//      super();
//      this.locationRepository = locationRepository;
//  }

//  public void checkPermission(@NonNull String[] permission, int requestCode, @NonNull int[] grantResults) {

//      if (requestCode == LOCATION_PERMISSION_CODE) {
//          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//              locationRepository.StartLocationRequest();

//          }

//          else {
//              locationRepository.StopLocationRequest();

//          }
//      }
//  }

//  public void PermissionAlreadyGranted(){

//      locationRepository.StartLocationRequest();

//  }

    private NearbyResultsUseCase nearbyResultsUseCase;

    private Application application;

    public MainActivityViewModel(@Nullable Application application, @Nullable NearbyResultsUseCase nearbyResultsUseCase) {
        super();
        this.nearbyResultsUseCase = nearbyResultsUseCase;
        this.application = application;
    }


    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(
                application, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_GRANTED);
            permissionGranted();

        }

//      else if (shouldShowRequestPermissionRationale(...)) {
//          // In an educational UI, explain to the user why your app requires this
//          // permission for a specific feature to behave as expected. In this UI,
//          // include a "cancel" or "no thanks" button that allows the user to
//          // continue using your app without granting the permission.
//          showInContextUI(...);

        else {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_ASKED);

        }
    }

    private void permissionGranted() {
        nearbyResultsUseCase.startLocationRequest();

    }

    public SingleLiveEvent<PermissionsViewAction> getActionSingleLiveEvent() {
        return actionSingleLiveEvent;

    }
}
