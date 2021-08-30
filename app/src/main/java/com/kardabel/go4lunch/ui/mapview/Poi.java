package com.kardabel.go4lunch.ui.mapview;

/**
 * Created by stéphane Warin OCR on 26/08/2021.
 */
public class Poi {

    private double latitude;
    private double longitude;
    private final String poiName;

    public Poi(double latitude, double longitude, String poiName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.poiName = poiName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPoiName() {
        return poiName;
    }
}
