package com.kardabel.go4lunch.ui.mapview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kardabel.go4lunch.di.ListViewViewModelFactory;



public class MapViewFragment extends SupportMapFragment implements OnMapReadyCallback {

    private MapViewViewModel mMapViewViewModel;
    private GoogleMap googleMap;

    public MapViewFragment()  { getMapAsync(this); }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // CONFIGURE MAPVIEWMODEL
            ListViewViewModelFactory listViewModelFactory = ListViewViewModelFactory.getInstance();
            mMapViewViewModel =
                    new ViewModelProvider(this, listViewModelFactory).get(MapViewViewModel.class);


            mMapViewViewModel.getMapViewStatePoiMutableLiveData().observe(this, new Observer<MapViewViewState>() {
                @Override
                public void onChanged(MapViewViewState mapViewViewState) {

                    // MOVE THE CAMERA TO THE USER LOCATION
                    LatLng userLocation = new LatLng(mapViewViewState.getUserLocation().getLatitude(), mapViewViewState.getUserLocation().getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                    // DISPLAY BLUE DOT FOR USER LOCATION
                    googleMap.setMyLocationEnabled(true);

                    // ZOOM IN, ANIMATE CAMERA
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());

                    // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(userLocation)      // Sets the center of the map to Mountain View
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    // SET EVERY POI ON THE MAP
                    for (Poi poi : mapViewViewState.getPoiList()) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(poi.getPoiLatLng().latitude, poi.getPoiLatLng().longitude)).title(poi.getPoiName()));
                    }
                    Log.d("pipo", "onChanged() called with: mapViewViewState = [" + mapViewViewState + "]");
                }
            });
        }
    }
}