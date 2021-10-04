package com.kardabel.go4lunch;

import static org.junit.Assert.assertEquals;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.pojo.Geometry;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.pojo.RestaurantLatLngLiteral;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.ui.listview.RestaurantsWrapperViewState;
import com.kardabel.go4lunch.ui.mapview.MapViewViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewViewState;
import com.kardabel.go4lunch.ui.mapview.Poi;
import com.kardabel.go4lunch.usecase.PlaceSearchResultsUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapViewViewModelTest {

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();


    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final PlaceSearchResultsUseCase placeSearchResultsUseCase = Mockito.mock(PlaceSearchResultsUseCase.class);

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<PlaceSearchResults> placeSearchResultsMutableLiveData = new MutableLiveData<>();

    private MapViewViewModel mapViewViewModel;

    @Before
    public void setUp(){

        // SETUP THE LOCATION
        Location location = Mockito.mock(Location.class);

        Mockito.doReturn(30.0).when(location).getLatitude();
        Mockito.doReturn(42.0).when(location).getLongitude();

        locationMutableLiveData.setValue(location);

        Mockito.doReturn(locationMutableLiveData).when(locationRepository).getLocationLiveData();


        // SETUP THE PLACE SEARCH USE CASE
        mapViewViewModel = new MapViewViewModel(locationRepository, placeSearchResultsUseCase);


    }

    @Test
    public void nominalCase() throws InterruptedException {
        // GIVEN
        placeSearchResultsMutableLiveData.setValue(getDefaultRestaurants());
        // WHEN
        MapViewViewState mapViewViewState = LiveDataTestUtils.getOrAwaitValue(mapViewViewModel.getMapViewStatePoiMutableLiveData());
        // THEN
        assertEquals(2, mapViewViewState.getPoiList().size());
    }



    private PlaceSearchResults getDefaultRestaurants(){

        return (PlaceSearchResults) Arrays.asList(
                new RestaurantSearch(
                        "235",
                        "hotel",
                        "0102",
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(false, null, null),
                        2,
                        25

                ),
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
    }

    private List<Photo> getPhoto(){

        return Arrays.asList(
                new Photo(
                        400,
                        400,
                        new ArrayList<>(),
                        "photoTest"

                )
        );
    }

    private MapViewViewState getDefaultMapViewViewState(){

        return (MapViewViewState) Arrays.asList(
                new Poi(
                        "hotel",
                        "235",
                        "0102",
                        new LatLng(30.0, 42.1)

                ),
                new Poi(
                        "pipo",
                        "450",
                        "bla",
                        new LatLng(32.1, 42.2)

                )
        );
    }
}
