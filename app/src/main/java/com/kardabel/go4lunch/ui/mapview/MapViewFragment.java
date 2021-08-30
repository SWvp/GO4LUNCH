package com.kardabel.go4lunch.ui.mapview;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapViewFragment extends SupportMapFragment implements OnMapReadyCallback {

    private MapViewViewModel mMapViewViewModel;
    private GoogleMap googleMap;

    public MapViewFragment()  {
        getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        mMapViewViewModel = new ViewModelProvider(this).get(MapViewViewModel.class);

        mMapViewViewModel.getMapViewStatePoiMutableLiveData().observe(this, new Observer<MapViewViewState>() {
            @Override
            public void onChanged(MapViewViewState mapViewViewState) {
                for (Poi poi : mapViewViewState.getPoiList()) {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(poi.getLatitude(), poi.getLongitude())).title(poi.getPoiName()));
                }
            }
        });
    }


}