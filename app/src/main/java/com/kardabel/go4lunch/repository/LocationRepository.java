package com.kardabel.go4lunch.repository;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kardabel.go4lunch.MainApplication;

public class LocationRepository {

    public static final int DEFAULT_UPDATE_INTERVAL = 30_000;
    public static final int FASTEST_UPDATE_INTERVAL = 10_000;
    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    public LocationRepository() {

        LocationServices.getFusedLocationProviderClient(MainApplication.getApplication()).requestLocationUpdates(
                LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(DEFAULT_UPDATE_INTERVAL).setFastestInterval(FASTEST_UPDATE_INTERVAL),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        locationMutableLiveData.setValue(locationResult.getLastLocation());
                    }
                },
                Looper.getMainLooper()
        );
    }
    public LiveData<Location> getLocationLiveData() {
        return locationMutableLiveData;
    }
}
