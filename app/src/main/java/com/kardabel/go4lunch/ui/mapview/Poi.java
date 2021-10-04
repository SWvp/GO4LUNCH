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
}
