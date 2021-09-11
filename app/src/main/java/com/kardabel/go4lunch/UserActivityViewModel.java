package com.kardabel.go4lunch;

import android.app.Application;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.repository.LocationRepository;

public class UserActivityViewModel extends ViewModel {

    private static final int LOCATION_PERMISSION_CODE = 100;

    private LocationRepository locationRepository;


    public UserActivityViewModel(@NonNull Application application, @Nullable LocationRepository locationRepository) {
        super();
        this.locationRepository = locationRepository;
    }

    public void CheckPermission(@NonNull String[] permission, int requestCode, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationRepository.StartLocationRequest();


            }
            else {
                locationRepository.StopLocationRequest();

            }
        }

    }
}
