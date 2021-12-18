package com.kardabel.go4lunch.ui.mapview;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

public class MapViewState {

    private final List<Poi> poiList;
    private final LatLng latLng;
    private final float zoom;

    public MapViewState(List<Poi> poiList, LatLng latLng, float zoom) {
        this.poiList = poiList;
        this.latLng = latLng;
        this.zoom = zoom;
    }

    public List<Poi> getPoiList() {
        return poiList;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public float getZoom() {
        return zoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapViewState that = (MapViewState) o;
        return Float.compare(that.zoom, zoom) == 0 &&
                Objects.equals(poiList, that.poiList) &&
                Objects.equals(latLng, that.latLng);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                poiList,
                latLng,
                zoom);

    }

    @NonNull
    @Override
    public String toString() {
        return "MapViewState{" +
                "poiList=" + poiList +
                ", latLng=" + latLng +
                ", zoom=" + zoom +
                '}';

    }
}
