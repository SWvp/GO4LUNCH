package com.kardabel.go4lunch; // TODO STEPHANE : attention au package, il doit être le même que le sujet du test

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.app.Application;
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
import com.kardabel.go4lunch.util.CurrentNumericDay;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RestaurantsViewModelTest {

    private static final double EXPECTED_LATITUDE = 30.0;
    private static final double EXPECTED_LONGITUDE = 42.0;

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final NearbySearchResultsUseCase nearbySearchResultsUseCase = Mockito.mock(NearbySearchResultsUseCase.class);
    private final RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase = Mockito.mock(RestaurantDetailsResultsUseCase.class);

    private final CurrentNumericDay currentNumericDay = Mockito.mock(CurrentNumericDay.class);

    private final Location location = Mockito.mock(Location.class);
    private final Application application = Mockito.mock(Application.class);
    private final Clock clock = Clock.fixed(
            LocalDateTime
                    .of(
                            LocalDate.of(2021, 10, 20),
                            LocalTime.of(10, 0)
                    )
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NearbySearchResults> nearbySearchResultsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<RestaurantDetailsResult>> restaurantDetailsResultsUseCaseMutableLiveData = new MutableLiveData<>();

    private RestaurantsViewModel mRestaurantsViewModel;

    @Before
    public void setUp() {
        // SETUP THE LOCATION VALUE
        Mockito.doReturn(EXPECTED_LATITUDE).when(location).getLatitude();
        Mockito.doReturn(EXPECTED_LONGITUDE).when(location).getLongitude();
        Mockito.doReturn(666F).when(location).distanceTo(any());

        // SETUP THE MOCK RETURN

        Mockito.doReturn("Open").when(application).getString(R.string.open);
        Mockito.doReturn("Closing soon").when(application).getString(R.string.closing_soon);
        Mockito.doReturn("Closed").when(application).getString(R.string.closed);
        Mockito.doReturn("Closed until").when(application).getString(R.string.closed_until);
        Mockito.doReturn("Closed today").when(application).getString(R.string.closed_today);
        Mockito.doReturn("Open 24/7").when(application).getString(R.string.open_h24);
        Mockito.doReturn("Open until").when(application).getString(R.string.open_until);
        Mockito.doReturn("Permanently closed").when(application).getString(R.string.permanently_closed);
        Mockito.doReturn("Opening hours unavailable").when(application).getString(R.string.opening_hours_unavailable);
        Mockito.doReturn("am").when(application).getString(R.string.am);
        Mockito.doReturn("pm").when(application).getString(R.string.pm);
        Mockito.doReturn("m").when(application).getString(R.string.m);


        Mockito.doReturn(locationMutableLiveData).when(locationRepository).getLocationLiveData();
        Mockito.doReturn(nearbySearchResultsMutableLiveData).when(nearbySearchResultsUseCase).getNearbySearchResultsLiveData();
        Mockito.doReturn(restaurantDetailsResultsUseCaseMutableLiveData).when(restaurantDetailsResultsUseCase).getPlaceDetailsResultLiveData();
        Mockito.doReturn(3).when(currentNumericDay).getCurrentNumericDay();

        // SET LIVEDATA VALUES
        locationMutableLiveData.setValue(location);
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        "First_Place_Id", // TODO STEPHANE A extraire (toutes les variables dans les IN & OUT)
                        new OpeningHours(
                                true,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "1300"),
                                                new Open(3, "0900")),
                                        new Periods(
                                                new Close(3, "1450"),
                                                new Open(3, "2300")

                                        ))
                        ),
                        "First_Phone_Number",
                        "First_Website"
                ),
                new RestaurantDetails(
                        "Second_Place_Id",
                        new OpeningHours(
                                true,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "1150"),
                                                new Open(3, "0800")),
                                        new Periods(
                                                new Close(3, "1700"),
                                                new Open(3, "2256")
                                        ))
                        ),
                        "Second_Phone_Number",
                        "Second_Website"
                )
        ));

        mRestaurantsViewModel = new RestaurantsViewModel(
                application,
                locationRepository,
                nearbySearchResultsUseCase,
                restaurantDetailsResultsUseCase,
                currentNumericDay,
                clock);
    }

    @Test
    public void nominalCase() {
        // WHEN
        LiveDataTestUtils.observeForTesting(mRestaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getDefaultRestaurantViewState(), restaurantsWrapperViewState);

            verify(locationRepository).getLocationLiveData();
            verify(nearbySearchResultsUseCase).getNearbySearchResultsLiveData();
            verify(restaurantDetailsResultsUseCase).getPlaceDetailsResultLiveData();
            verifyNoMoreInteractions(locationRepository, nearbySearchResultsUseCase, restaurantDetailsResultsUseCase);
        });
    }

    ////////// OPENING HOURS TESTS ///////////

    @Test
    public void open_Restaurant_Should_Display_Open_Until_Closing_Time(){
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        "First_Place_Id",
                        new OpeningHours(
                                true,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "1050"),
                                                new Open(3, "0900")),
                                        new Periods(
                                                new Close(3, "1450"),
                                                new Open(3, "2300")

                                        ))
                        ),
                        "First_Phone_Number",
                        "First_Website"
                ),
                new RestaurantDetails(
                        "Second_Place_Id",
                        new OpeningHours(
                                true,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "1010"),
                                                new Open(3, "0800")),
                                        new Periods(
                                                new Close(3, "1700"),
                                                new Open(3, "2256")
                                        ))
                        ),
                        "Second_Phone_Number",
                        "Second_Website"
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(mRestaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getRestaurantsClosingSoon(), restaurantsWrapperViewState);

            verify(locationRepository).getLocationLiveData();
            verify(nearbySearchResultsUseCase).getNearbySearchResultsLiveData();
            verify(restaurantDetailsResultsUseCase).getPlaceDetailsResultLiveData();
            verifyNoMoreInteractions(locationRepository, nearbySearchResultsUseCase, restaurantDetailsResultsUseCase);
        });

    }

    @Test
    public void before_One_Hour_Closing_Time_Should_Display_Closing_Soon(){
        // GIVEN

        // WHEN
        // THEN

    }

    @Test
    public void never_Closing_Should_Display_H24(){
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getPermanentlyOpenRestaurantsDetails());
        // WHEN
        LiveDataTestUtils.observeForTesting(mRestaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getPermanentlyOpenRestaurantViewState(), restaurantsWrapperViewState);

        });
    }

    @Test
    public void permanently_Closed_Should_Display_This_Status(){
        // GIVEN
        // WHEN
        // THEN

    }

    @Test
    public void after_Closing_Time_Should_Display_Closed(){
        // GIVEN
        // WHEN
        // THEN

    }

    @Test
    public void without_Periods_List_But_With_OpenNow_Should_Display_Open(){
        // GIVEN
        // WHEN
        // THEN

    }

    @Test
    public void without_Periods_List_But_With_ClosedNow_Should_Display_Closed(){
        // GIVEN
        // WHEN
        // THEN

    }

    @Test
    public void restaurant_Closed_Until_Tomorrow_Should_Display_Closed(){
        // GIVEN
        // WHEN
        // THEN

    }


    ////////// RATING STARS TESTS ///////////

    @Test
    public void forStarsShouldDisplayTwoStars(){
        // GIVEN
        // WHEN
        // THEN

    }

    @Test
    public void zeroStarsShouldDisplayZeroStars(){
        // GIVEN
        // WHEN
        // THEN

    }

    /////////// RESTAURANTS LIST TEST ///////////

    @Test
    public void whenDetailsListIsEmptyShouldDisplayNearbyList(){
        // GIVEN
        // WHEN
        // THEN

    }

    /////////// PHOTO TESTS ///////////

    @Test
    public void whenPhotoListIsNullShouldDisplayPhotoUnavailable(){
        // GIVEN
        // WHEN
        // THEN

    }

    @Test
    public void whenPhotoReferenceDontExistShouldDisplayPhotoUnavailable(){
        // GIVEN
        // WHEN
        // THEN

    }





    // region IN

    private List<RestaurantSearch> getDefaultRestaurants() {
               List<RestaurantSearch> restaurants = new ArrayList<>();
        restaurants.add(
                new RestaurantSearch(
                        "First_Place_Id", // TODO STEPHANE A extraire
                        "First_Name",
                        "First_Address",
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(true, null),
                        2,
                        25,
                        false
                )
        );
        restaurants.add(
                new RestaurantSearch(
                        "Second_Place_Id",
                        "Second_Name",
                        "Second_Address",
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(true, null),
                        4,
                        25,
                        false
                )
        );
        return restaurants;

    }

    private List<RestaurantDetailsResult> getDefaultRestaurantsDetails(RestaurantDetails... restaurantDetails) {

        List<RestaurantDetailsResult> results = new ArrayList<>();

        for(RestaurantDetails restaurantDetail : restaurantDetails){
            results.add(new RestaurantDetailsResult(restaurantDetail));

        }
        return results;

    }

    private List<Periods> getDefaultPeriod(Periods... periods) {
        List<Periods> results = new ArrayList<>();

        for (Periods period : periods) {
            results.add(new Periods(
                    period.getClose(),
                    period.getOpen()));

        }
        return results;

    }


    private List<RestaurantDetailsResult> getPermanentlyOpenRestaurantsDetails() {
        return Arrays.asList(
                new RestaurantDetailsResult(new RestaurantDetails(
                        "235",
                        new OpeningHours(true, getPermanentlyOpenPeriods()),
                        "0145204520",
                        "www.coucou.com"

                )),
                new RestaurantDetailsResult(new RestaurantDetails(
                        "450",
                        new OpeningHours(true, getPermanentlyOpenPeriods()),
                        "0625656488",
                        "www.bonjour.fr"

                ))

        );
    }


    private List<Periods> getPermanentlyOpenPeriods() {
        return Collections.singletonList(
                new Periods(
                        new Close(0, "0000"),
                        new Open(0, "0000"))

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


    /////////////////////////////////////////////////////////



    // region OUT

    private RestaurantsWrapperViewState getDefaultRestaurantViewState(){
        return new RestaurantsWrapperViewState(getRestaurantsResults());

    }

    private List<RestaurantsViewState> getRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                "First_Name",
                                "First_Address",
                                "photoTest",
                                "666m",
                                "Open until 1.pm",
                                1,
                                "First_Place_Id"),

                new RestaurantsViewState
                        (
                                "Second_Name",
                                "Second_Address",
                                "photoTest",
                                "666m",
                                "Open until 11:50.am",
                                2,
                                "Second_Place_Id")

        );
    }

    private RestaurantsWrapperViewState getRestaurantsClosingSoon(){
        return new RestaurantsWrapperViewState(getRestaurantsClosingSoonResults());

    }

    private List<RestaurantsViewState> getRestaurantsClosingSoonResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                "First_Name",
                                "First_Address",
                                "photoTest",
                                "666m",
                                "Closing soon",
                                1,
                                "First_Place_Id"),

                new RestaurantsViewState
                        (
                                "Second_Name",
                                "Second_Address",
                                "photoTest",
                                "666m",
                                "Closing soon",
                                2,
                                "Second_Place_Id")

        );
    }



    private List<RestaurantsViewState> getRestaurantsWithoutDetails() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                "First_Name",
                                "First_Address",
                                "photoTest",
                                "666m",
                                "Open",
                                1,
                                "First_Place_Id"),

                new RestaurantsViewState
                        (
                                "Second_Name",
                                "Second_Address",
                                "photoTest",
                                "666m",
                                "Open",
                                2,
                                "Second_Place_Id")

        );
    }



    private RestaurantsWrapperViewState getPermanentlyOpenRestaurantViewState(){
        return new RestaurantsWrapperViewState(getPermanentlyRestaurantsResults());

    }

    private List<RestaurantsViewState> getPermanentlyRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                "hotel",
                                "0102",
                                "photoTest",
                                "666m",
                                "Open 24/7",
                                1,
                                "235"),

                new RestaurantsViewState
                        (
                                "pipo",
                                "bla",
                                "photoTest",
                                "666m",
                                "Open 24/7",
                                2,
                                "450")

        );
    }
    // endregion

}
