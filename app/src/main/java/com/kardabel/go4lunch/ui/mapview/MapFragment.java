package com.kardabel.go4lunch.ui.mapview;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;
import com.kardabel.go4lunch.util.SvgToBitmapConverter;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public MapFragment()  { getMapAsync(this); }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        // CUSTOM MAP WITHOUT POI WE DON'T NEED
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // CONFIGURE MAPVIEWMODEL
            ViewModelFactory listViewModelFactory = ViewModelFactory.getInstance();
            MapViewModel mapViewModel =
                    new ViewModelProvider(this, listViewModelFactory).get(MapViewModel.class);

            mapViewModel.getMapViewStateLiveData().observe(this, mapViewState -> {
                googleMap.clear();

                // MOVE THE CAMERA TO THE USER LOCATION
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapViewState.getLatLng(), mapViewState.getZoom()));

                // DISPLAY BLUE DOT FOR USER LOCATION
                googleMap.setMyLocationEnabled(true);

                // ZOOM IN, ANIMATE CAMERA
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());

                // CAMERA POSITION
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mapViewState.getLatLng())      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // SET EVERY POI ON THE MAP
                for (Poi poi : mapViewState.getPoiList()) {
                    Marker marker;
                    if(poi.getIsFavorite()){
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(poi.getPoiLatLng())
                                .title(poi.getPoiName())
                                .snippet(poi.getPoiAddress())
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(SvgToBitmapConverter
                                                .getBitmapFromVectorDrawable(requireContext(),
                                                        R.drawable.restaurant_poi_icon_green))));
                        // SET TAG TO RETRIEVE THE MARKER IN onMarkerClick METHOD
                    }else{
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(poi.getPoiLatLng())
                                .title(poi.getPoiName())
                                .snippet(poi.getPoiAddress())
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(SvgToBitmapConverter
                                                .getBitmapFromVectorDrawable(requireContext(),
                                                        R.drawable.restaurant_poi_icon_red))));
                        // SET TAG TO RETRIEVE THE MARKER IN onMarkerClick METHOD


                    }
                    assert marker != null;
                    marker.setTag(poi.getPoiPlaceId());
                }
            });
            // SET A LISTENER FOR MARKER CLICK
            googleMap.setOnMarkerClickListener(this);

        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String placeId = (String) marker.getTag();
        startActivity(RestaurantDetailsActivity.navigate(requireContext(), placeId));
        return false;

    }
}