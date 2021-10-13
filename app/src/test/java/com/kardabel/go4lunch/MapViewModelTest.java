package com.kardabel.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
import java.util.Collections;
import java.util.List;

public class MapViewModelTest {

    private static final double EXPECTED_LATITUDE = 30.0;
    private static final double EXPECTED_LONGITUDE = 42.0;
    private static final float EXPECTED_ZOOM_FOCUS = 15F;

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final NearbySearchResultsUseCase nearbySearchResultsUseCase = Mockito.mock(NearbySearchResultsUseCase.class);

    private final Location location = Mockito.mock(Location.class);

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NearbySearchResults> nearbySearchResultsMutableLiveData = new MutableLiveData<>();

    private MapViewModel mMapViewModel;

    @Before
    public void setUp() {
        // SETUP THE LOCATION VALUE
        Mockito.doReturn(EXPECTED_LATITUDE).when(location).getLatitude();
        Mockito.doReturn(EXPECTED_LONGITUDE).when(location).getLongitude();

        // SETUP THE MOCK RETURN
        Mockito.doReturn(locationMutableLiveData).when(locationRepository).getLocationLiveData();
        Mockito.doReturn(nearbySearchResultsMutableLiveData).when(nearbySearchResultsUseCase).getNearbySearchResultsLiveData();

        // SET LIVEDATA VALUES
        locationMutableLiveData.setValue(location);
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));

        mMapViewModel = new MapViewModel(locationRepository, nearbySearchResultsUseCase);
    }

    @Test
    public void nominalCase() {
        // WHEN
        LiveDataTestUtils.observeForTesting(mMapViewModel.getMapViewStateLiveData(), mapViewState -> {
            // THEN
            assertEquals(MapViewModelTest.this.getDefaultMapViewState(), mapViewState);

            verify(locationRepository).getLocationLiveData();
            verify(nearbySearchResultsUseCase).getNearbySearchResultsLiveData();
            verifyNoMoreInteractions(locationRepository, nearbySearchResultsUseCase);
        });
    }

    // region IN
    private List<RestaurantSearch> getDefaultRestaurants() {
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
                        25,
                        false
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
                        25,
                        false
                )
        );
        return restaurants;

    }

    private List<Photo> getPhoto() {

        return Collections.singletonList(
                new Photo(
                        400,
                        400,
                        new ArrayList<>(),
                        "photoTest"

                )
        );
    }
    // endregion

    // region OUT
    private MapViewState getDefaultMapViewState() {
        return new MapViewState(
                getPoi(),
                new LatLng(EXPECTED_LATITUDE, EXPECTED_LONGITUDE), EXPECTED_ZOOM_FOCUS);
    }

    private List<Poi> getPoi() {
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
    // endregion OUT

}
