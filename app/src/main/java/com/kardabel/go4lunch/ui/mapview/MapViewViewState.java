package com.kardabel.go4lunch.ui.mapview;

import java.util.List;

public class MapViewViewState {

    private final List <Poi> poiList;

    public MapViewViewState(List<Poi> poiList) {
        this.poiList = poiList;
    }

    public List<Poi> getPoiList() {
        return poiList;
    }
}
