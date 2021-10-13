package com.kardabel.go4lunch;

import static org.junit.Assert.assertEquals;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.Close;
import com.kardabel.go4lunch.pojo.Geometry;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.Open;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Periods;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetails;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.RestaurantLatLngLiteral;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.ui.listview.RestaurantsViewModel;
import com.kardabel.go4lunch.ui.listview.RestaurantsViewState;
import com.kardabel.go4lunch.ui.listview.RestaurantsWrapperViewState;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.util.CurrentConvertedHour;
import com.kardabel.go4lunch.util.CurrentNumericDay;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListViewModelTest {

    private static final double EXPECTED_LATITUDE = 30.0;
    private static final double EXPECTED_LONGITUDE = 42.0;

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final NearbySearchResultsUseCase nearbySearchResultsUseCase = Mockito.mock(NearbySearchResultsUseCase.class);
    private final RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase = Mockito.mock(RestaurantDetailsResultsUseCase.class);

    private final CurrentNumericDay currentNumericDay = Mockito.mock(CurrentNumericDay.class);
    private final CurrentConvertedHour currentConvertedHour = Mockito.mock(CurrentConvertedHour.class);

    private final Location location = Mockito.mock(Location.class);

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NearbySearchResults> nearbySearchResultsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<RestaurantDetailsResult>> restaurantDetailsResultsUseCaseMutableLiveData = new MutableLiveData<>();

    private RestaurantsViewModel mRestaurantsViewModel;

    @Before
    public void setUp() {
        // SETUP THE LOCATION VALUE
        Mockito.doReturn(EXPECTED_LATITUDE).when(location).getLatitude();
        Mockito.doReturn(EXPECTED_LONGITUDE).when(location).getLongitude();

        // SETUP THE MOCK RETURN
        Mockito.doReturn(locationMutableLiveData).when(locationRepository).getLocationLiveData();
        Mockito.doReturn(nearbySearchResultsMutableLiveData).when(nearbySearchResultsUseCase).getNearbySearchResultsLiveData();
        Mockito.doReturn(restaurantDetailsResultsUseCaseMutableLiveData).when(restaurantDetailsResultsUseCase).getPlaceDetailsResultLiveData();
        Mockito.doReturn(2).when(currentNumericDay).getCurrentNumericDay();
        Mockito.doReturn(1100).when(currentConvertedHour).getCurrentConvertedHour();

        // SET LIVEDATA VALUES
        locationMutableLiveData.setValue(location);
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails());

        mRestaurantsViewModel = new RestaurantsViewModel(locationRepository, nearbySearchResultsUseCase, restaurantDetailsResultsUseCase, currentNumericDay, currentConvertedHour);
    }

    @Test
    public void nominalCase() {
        // WHEN
        LiveDataTestUtils.observeForTesting(mRestaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getDefaultRestaurantViewState(), restaurantsWrapperViewState);
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
                        new OpeningHours(true, null, null),
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
                        new OpeningHours(true, null, null),
                        2,
                        25,
                        false
                )
        );
        return restaurants;

    }

    private List<RestaurantDetailsResult> getDefaultRestaurantsDetails() {
        return Arrays.asList(
                new RestaurantDetailsResult(new RestaurantDetails(
                        "235",
                        new OpeningHours(true, getFirstPeriods(), null),
                        "0145204520",
                        "www.coucou.com"

                        )),
                new RestaurantDetailsResult(new RestaurantDetails(
                        "450",
                        new OpeningHours(true, getSecondPeriods(), null),
                        "0625656488",
                        "www.bonjour.fr"

                ))
        );
    }

    private List<Periods> getFirstPeriods() {
        return Arrays.asList(
                new Periods(
                        new Close(2, "1125"),
                        new Open(2, "1000")),
                new Periods(
                        new Close(3, "1125"),
                        new Open(3, "1000"))

                );
    }

    private List<Periods> getSecondPeriods() {
        return Arrays.asList(
                new Periods(
                        new Close(3, "0100"),
                        new Open(2, "1000")),
                new Periods(
                        new Close(4, "1125"),
                        new Open(3, "1000"))

        );
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
    private RestaurantsWrapperViewState getDefaultRestaurantViewState(){
        return new RestaurantsWrapperViewState(getRestaurants());

    }

    private List<RestaurantsViewState> getRestaurants() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                "hotel",
                                "0102",
                                "photoTest",
                                "23m",
                                "Closing soon",
                                2,
                                "235"),

                new RestaurantsViewState
                        (
                                "pipo",
                                "bla",
                                "photoTest",
                                "23m",
                                "Open until 1.am",
                                2,
                                "450")

        );
    }
    // endregion

}
