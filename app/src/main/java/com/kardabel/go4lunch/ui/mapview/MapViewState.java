package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class MapViewState {

    private final List <Poi> poiList;
    private final Location userLocation;

    public MapViewState(List<Poi> poiList, Location userLocation) {
        this.poiList = poiList;
        this.userLocation = userLocation;

    }

    public List<Poi> getPoiList() { return poiList; }

    public Location getUserLocation() { return userLocation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapViewState that = (MapViewState) o;
        return Objects.equals(poiList, that.poiList) && Objects.equals(userLocation, that.userLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poiList, userLocation);
    }

    @Override
    public String toString() {
        return "MapViewState{" +
                "poiList=" + poiList +
                ", userLocation=" + userLocation +
                '}';
    }
}
