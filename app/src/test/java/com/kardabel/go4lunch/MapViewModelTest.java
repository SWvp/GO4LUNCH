package com.kardabel.go4lunch;

import static org.junit.Assert.assertEquals;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.pojo.Geometry;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantLatLngLiteral;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.ui.mapview.MapViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewState;
import com.kardabel.go4lunch.ui.mapview.Poi;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapViewModelTest {

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final NearbySearchResultsUseCase nearbySearchResultsUseCase = Mockito.mock(NearbySearchResultsUseCase.class);
    private final NearbySearchResponseRepository nearbySearchResponseRepository = Mockito.mock(NearbySearchResponseRepository.class);

    private final Location location = Mockito.mock(Location.class);
    private final Location newLocation = Mockito.mock(Location.class);

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NearbySearchResults> nearbySearchResultsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NearbySearchResults> nearbySearchResponseRepositoryMutableLiveData = new MutableLiveData<>();

    private MapViewModel mMapViewModel;

    private final double lat = 30.0;
    private final double lng = 42.0;

    private final double newLat = 28.2;
    private final double newLng = 25.3;

    private final String latStr = lat + "";
    private final String lngStr = lng + "";

    private final String newLatStr = newLat + "";
    private final String newLngStr = newLng + "";

    @Before
    public void setUp(){

        // SETUP THE LOCATION
        Mockito.doReturn(lat).when(location).getLatitude();
        Mockito.doReturn(lng).when(location).getLongitude();

        // SETUP THE NEW LOCATION
        Mockito.doReturn(newLat).when(newLocation).getLatitude();
        Mockito.doReturn(newLng).when(newLocation).getLongitude();


        Mockito.doReturn(locationMutableLiveData).when(locationRepository).getLocationLiveData();

        // CONVERT LOCATION TO STRING
        String latStr = lat + "";
        String lngStr = lng + "";

        Mockito.doReturn(nearbySearchResponseRepositoryMutableLiveData).when(nearbySearchResponseRepository).getRestaurantListLiveData(
                "restaurant",
                latStr + "," + lngStr,
                "1000");
        Mockito.doReturn(nearbySearchResultsMutableLiveData).when(nearbySearchResultsUseCase).getNearbySearchResultsLiveData();


        mMapViewModel = new MapViewModel(locationRepository, nearbySearchResultsUseCase);

    }

    @Test
    public void nominalCase() throws InterruptedException {
        // GIVEN
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));
        // WHEN
        MapViewState result = LiveDataTestUtils.getOrAwaitValue(mMapViewModel.getMapViewStateLiveData());
        // THEN
        assertEquals(2, result.getPoiList().size());
        assertEquals(result, new MapViewState(getPoi(), locationRepository.getLocationLiveData().getValue()));

    }

    @Test
    public void whenLocationIsUpdated() throws InterruptedException {
        // GIVEN
        locationMutableLiveData.setValue(location);
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));
        nearbySearchResponseRepositoryMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));
        // WHEN
        MapViewState result = LiveDataTestUtils.getOrAwaitValue(mMapViewModel.getMapViewStateLiveData());
        // THEN
        assertEquals(result, new MapViewState(getPoi(), locationRepository.getLocationLiveData().getValue()));
        //Mockito.verify(nearbySearchResponseRepository).getRestaurantListLiveData(
         //       "restaurant",
         //       latStr + "," + lngStr,
         //       "1000");


    }

    @Test
    public void whenLocationIsNotTheSameInRepo(){
        // GIVEN
        locationMutableLiveData.setValue(newLocation);

        // WHEN


        // THEN

        Mockito.verifyNoInteractions(nearbySearchResponseRepository);
    }

    private  List<RestaurantSearch> getDefaultRestaurants(){
        List<RestaurantSearch> restaurants = new ArrayList<>();
        restaurants.add(
                new RestaurantSearch(
                        "235",
                        "hotel",
                        "0102",
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(false, null, null),
                        2,
                        25

                )
        );
        restaurants.add(
                new RestaurantSearch(
                        "450",
                        "pipo",
                        "bla",
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(false, null, null),
                        2,
                        25
                )
        );
        return restaurants;

    }

    private List<Poi> getPoi(){
        List<Poi> poi = new ArrayList<>();
        poi.add(new Poi(
                "hotel",
                "235",
                "0102",
                new LatLng(30.0, 42.1)
        ));
        poi.add(new Poi(
                "pipo",
                "450",
                "bla",
                new LatLng(32.1, 42.2)
        ));
        return poi;
    }

    private List<Photo> getPhoto(){

        return Collections.singletonList(
                new Photo(
                        400,
                        400,
                        new ArrayList<>(),
                        "photoTest"

                )
        );
    }
}
