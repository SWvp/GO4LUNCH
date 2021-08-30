package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.repository.LocationRepository;

import java.util.ArrayList;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private MediatorLiveData<MapViewViewState> mapViewStatePoiMediatorLiveData = new MediatorLiveData<>();
    private LocationRepository locationRepository = new LocationRepository();
    private LiveData<Location> mLocationLiveData = locationRepository.getLocationLiveData();

    public MapViewViewModel() {
        mapViewStatePoiMediatorLiveData.addSource(mLocationLiveData, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                combine(location);
            }
        });
    }

    public void combine(@Nullable Location location){
        if(location == null){ return; }

        List<Poi> poiList = new ArrayList<>();
        poiList.add(new Poi(location.getLatitude(), location.getLongitude(), "home"));
        MapViewViewState mapViewViewState = new MapViewViewState(poiList);

        mapViewStatePoiMediatorLiveData.setValue(mapViewViewState);

    }

    public LiveData<MapViewViewState> getMapViewStatePoiMutableLiveData() {
        return mapViewStatePoiMediatorLiveData;
    }

}