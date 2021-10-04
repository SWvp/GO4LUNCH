package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.List;

public class MapViewViewState {

    private final List <Poi> poiList;
    private final Location userLocation;

    public MapViewViewState(List<Poi> poiList, Location userLocation) {
        this.poiList = poiList;
        this.userLocation = userLocation;

    }

    public List<Poi> getPoiList() { return poiList; }

    public Location getUserLocation() { return userLocation; }

}
