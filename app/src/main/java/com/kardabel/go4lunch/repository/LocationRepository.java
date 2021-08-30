package com.kardabel.go4lunch.repository;


import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kardabel.go4lunch.MainActivity;
import com.kardabel.go4lunch.MainApplication;

public class LocationRepository {

    private MutableLiveData<Location> mLocationMutableLiveData = new MutableLiveData<>();

    public LocationRepository() {

        LocationServices.getFusedLocationProviderClient(MainApplication.getApplication()).requestLocationUpdates(
                LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10_000),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        mLocationMutableLiveData.setValue(locationResult.getLastLocation());
                    }
                },
                Looper.getMainLooper()
        );
    }
    public LiveData<Location> getLocationLiveData() {
        return mLocationMutableLiveData;
    }
}
