package com.kardabel.go4lunch.ui.mapview;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class Poi {


    private final String poiName;
    private final String poiPlaceId;
    private final String poiAddress;
    private final LatLng poiLatLng;

    public Poi(String poiName, String poiPlaceId, String poiAddress, LatLng poiLatLng) {
        this.poiName = poiName;
        this.poiPlaceId = poiPlaceId;
        this.poiAddress = poiAddress;
        this.poiLatLng = poiLatLng;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poi poi = (Poi) o;
        return Objects.equals(poiName, poi.poiName) &&
                Objects.equals(poiPlaceId, poi.poiPlaceId) &&
                Objects.equals(poiAddress, poi.poiAddress) &&
                Objects.equals(poiLatLng, poi.poiLatLng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poiName, poiPlaceId, poiAddress, poiLatLng);
    }

    @Override
    public String toString() {
        return "Poi{" +
                "poiName='" + poiName + '\'' +
                ", poiPlaceId='" + poiPlaceId + '\'' +
                ", poiAddress='" + poiAddress + '\'' +
                ", poiLatLng=" + poiLatLng +
                '}';
    }
}
