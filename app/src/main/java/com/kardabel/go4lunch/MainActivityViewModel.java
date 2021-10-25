package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.util.SingleLiveEvent;

public class MainActivityViewModel extends ViewModel {

    private SingleLiveEvent<PermissionsViewAction> actionSingleLiveEvent = new SingleLiveEvent<>();

    private LocationRepository locationRepository;
    private Application application;

    public MainActivityViewModel(@Nullable Application application, @Nullable LocationRepository locationRepository) {
        super();
        this.application = application;
        this.locationRepository = locationRepository;

    }

    public void checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(application, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_GRANTED);
            permissionGranted();

        }

  //    else if(ContextCompat.checkSelfPermission(application, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
  //        actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_ASKED);

  //    }

  //    else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)) {
  //        actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_DENIED);

  //    }

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
