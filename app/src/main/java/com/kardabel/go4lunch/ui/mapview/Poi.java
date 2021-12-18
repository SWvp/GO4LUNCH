package com.kardabel.go4lunch.ui.mapview;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class Poi {


    private final String poiName;
    private final String poiPlaceId;
    private final String poiAddress;
    private final LatLng poiLatLng;
    private final boolean isFavorite;

    public Poi(String poiName, String poiPlaceId, String poiAddress, LatLng poiLatLng, boolean isFavorite) {
        this.poiName = poiName;
        this.poiPlaceId = poiPlaceId;
        this.poiAddress = poiAddress;
        this.poiLatLng = poiLatLng;
        this.isFavorite = isFavorite;
    }

    public String getPoiName() {
        return poiName;
    }

    public String getPoiPlaceId() {
        return poiPlaceId;
    }

    public String getPoiAddress() {
        return poiAddress;
    }

    public LatLng getPoiLatLng() {
        return poiLatLng;
    }

    public boolean getIsFavorite() { return isFavorite; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poi poi = (Poi) o;
        return isFavorite == poi.isFavorite
                && Objects.equals(poiName, poi.poiName)
                && Objects.equals(poiPlaceId, poi.poiPlaceId)
                && Objects.equals(poiAddress, poi.poiAddress)
                && Objects.equals(poiLatLng, poi.poiLatLng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                poiName,
                poiPlaceId,
                poiAddress,
                poiLatLng,
                isFavorite);
    }

    @NonNull
    @Override
    public String toString() {
        return "Poi{" +
                "poiName='" + poiName + '\'' +
                ", poiPlaceId='" + poiPlaceId + '\'' +
                ", poiAddress='" + poiAddress + '\'' +
                ", poiLatLng=" + poiLatLng +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
