package com.kardabel.go4lunch.ui.restaurants;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.app.Application;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
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
import com.kardabel.go4lunch.pojo.Restaurant;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.UserSearchRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsUseCase;

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

    // MOCK THE INJECTIONS
    private final Application application =
            Mockito.mock(Application.class);
    private final LocationRepository locationRepository =
            Mockito.mock(LocationRepository.class);
    private final GetNearbySearchResultsUseCase getNearbySearchResultsUseCase =
            Mockito.mock(GetNearbySearchResultsUseCase.class);
    private final GetRestaurantDetailsResultsUseCase getRestaurantDetailsResultsUseCase =
            Mockito.mock(GetRestaurantDetailsResultsUseCase.class);
    private final UsersWhoMadeRestaurantChoiceRepository usersWhoMadeRestaurantChoiceRepository =
            Mockito.mock(UsersWhoMadeRestaurantChoiceRepository.class);
    private final UserSearchRepository userSearchRepository =
            Mockito.mock(UserSearchRepository.class);
    private final Clock clock = Clock.fixed(
            LocalDateTime
                    .of(
                            LocalDate.of(2021, 10, 20),
                            LocalTime.of(10, 0)
                    )
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );
    private final Clock clockNewMonth = Clock.fixed(
            LocalDateTime
                    .of(
                            LocalDate.of(2021, 10, 31),
                            LocalTime.of(10, 0)
                    )
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );
    private final Clock clockNewYear = Clock.fixed(
            LocalDateTime
                    .of(
                            LocalDate.of(2021, 12, 31),
                            LocalTime.of(10, 0)
                    )
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );
    private final Location location =
            Mockito.mock(Location.class);

    // CREATE MUTABLE
    private final MutableLiveData<Location> locationMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<NearbySearchResults> nearbySearchResultsMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<RestaurantDetailsResult>> restaurantDetailsResultsUseCaseMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<UserWhoMadeRestaurantChoice>> usersWhoMadeRestaurantChoiceMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<String> userSearchMutableLiveData =
            new MutableLiveData<>();


    private RestaurantsViewModel restaurantsViewModel;

    @Before
    public void setUp() {

        // STRINGS RETURNS
        Mockito.doReturn("Open").when(application).getString(R.string.open);
        Mockito.doReturn("Closing soon").when(application).getString(R.string.closing_soon);
        Mockito.doReturn("Closed").when(application).getString(R.string.closed);
        Mockito.doReturn("Closed until").when(application).getString(R.string.closed_until);
        Mockito.doReturn("Closed today").when(application).getString(R.string.closed_today);
        Mockito.doReturn("Open 24/7").when(application).getString(R.string.open_h24);
        Mockito.doReturn("Open until").when(application).getString(R.string.open_until);
        Mockito.doReturn("Tomorrow").when(application).getString(R.string.tomorrow);
        Mockito.doReturn("Permanently closed").when(application).getString(R.string.permanently_closed);
        Mockito.doReturn("Opening hours unavailable").when(application).getString(R.string.opening_hours_unavailable);
        Mockito.doReturn("Photo unavailable").when(application).getString(R.string.photo_unavailable);
        Mockito.doReturn("https://maps.googleapis.com/maps/api/place/").when(application).getString(R.string.api_url);
        Mockito.doReturn("photo?maxwidth=300&photo_reference=").when(application).getString(R.string.photo_reference);
        Mockito.doReturn("&key=").when(application).getString(R.string.and_key);
        Mockito.doReturn("(").when(application).getString(R.string.left_bracket);
        Mockito.doReturn(")").when(application).getString(R.string.right_bracket);
        Mockito.doReturn("Distance unavailable").when(application).getString(R.string.distance_unavailable);
        Mockito.doReturn("am").when(application).getString(R.string.am);
        Mockito.doReturn("pm").when(application).getString(R.string.pm);
        Mockito.doReturn("m").when(application).getString(R.string.m);
        Mockito.doReturn(":").when(application).getString(R.string.two_dots);
        Mockito.doReturn(":0").when(application).getString(R.string.two_dots_for_minutes);
        Mockito.doReturn(".").when(application).getString(R.string.dot);

        // SETUP THE LOCATION VALUE
        Mockito.doReturn(EXPECTED_LATITUDE).when(location).getLatitude();
        Mockito.doReturn(EXPECTED_LONGITUDE).when(location).getLongitude();
        Mockito.doReturn(0F).when(location).distanceTo(any());

        // RETURNS OF MOCKED CLASS
        Mockito.doReturn(locationMutableLiveData)
                .when(locationRepository)
                .getLocationLiveData();
        Mockito.doReturn(nearbySearchResultsMutableLiveData)
                .when(getNearbySearchResultsUseCase)
                .invoke();
        Mockito.doReturn(restaurantDetailsResultsUseCaseMutableLiveData)
                .when(getRestaurantDetailsResultsUseCase)
                .invoke();
        Mockito.doReturn(usersWhoMadeRestaurantChoiceMutableLiveData)
                .when(usersWhoMadeRestaurantChoiceRepository)
                .getWorkmatesWhoMadeRestaurantChoice();
        Mockito.doReturn(userSearchMutableLiveData)
                .when(userSearchRepository)
                .getUsersSearchLiveData();


        // SET LIVEDATA VALUES
        locationMutableLiveData.setValue(location);
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
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
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
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
                        secondNumber,
                        secondSite
                )
        ));
        usersWhoMadeRestaurantChoiceMutableLiveData.setValue(userWhoMadeChoice());
        userSearchMutableLiveData.setValue(null);

        restaurantsViewModel = new RestaurantsViewModel(
                application,
                locationRepository,
                getNearbySearchResultsUseCase,
                getRestaurantDetailsResultsUseCase,
                usersWhoMadeRestaurantChoiceRepository,
                userSearchRepository,
                clock);
    }

    @Test
    public void nominalCase() {
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(RestaurantsViewModelTest.this.getDefaultRestaurantViewState(), restaurantsWrapperViewState);

            verify(locationRepository).getLocationLiveData();
            verify(getNearbySearchResultsUseCase).invoke();
            verify(getRestaurantDetailsResultsUseCase).invoke();
            verifyNoMoreInteractions(
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    getRestaurantDetailsResultsUseCase);
        });
    }

    ////////// OPENING HOURS TESTS ///////////

    @Test
    public void before_One_Hour_Closing_Time_Should_Display_Closing_Soon() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
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
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
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
                        secondNumber,
                        secondSite
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getRestaurantsClosingSoon(), restaurantsWrapperViewState);

            verify(locationRepository).getLocationLiveData();
            verify(getNearbySearchResultsUseCase).invoke();
            verify(getRestaurantDetailsResultsUseCase).invoke();
            verifyNoMoreInteractions(locationRepository, getNearbySearchResultsUseCase, getRestaurantDetailsResultsUseCase);
        });

    }

    @Test
    public void never_Closing_Should_Display_H24() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(
                                true,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "0000"),
                                                new Open(3, "0000"))
                                )
                        ),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(
                                true,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "1800"),
                                                new Open(3, "1800"))
                                )
                        ),
                        secondNumber,
                        secondSite
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getPermanentlyOpenRestaurant(), restaurantsWrapperViewState);

        });
    }

    @Test
    public void permanently_Closed_Should_Display_This_Status() {
        // GIVEN
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getPermanentlyClosedRestaurants()));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getPermanentlyClosedRestaurantsViewState(), restaurantsWrapperViewState);

        });
    }

    @Test
    public void after_Closing_Time_Should_Display_Closed_Until() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "0900"),
                                                new Open(3, "0800")),
                                        new Periods(
                                                new Close(3, "2300"),
                                                new Open(3, "1800"))

                                )
                        ),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "1900"),
                                                new Open(3, "1100")),
                                        new Periods(
                                                new Close(3, "2200"),
                                                new Open(3, "2000"))
                                )
                        ),
                        secondNumber,
                        secondSite
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getRestaurantsClosed(), restaurantsWrapperViewState);

        });

    }

    @Test
    public void without_Periods_List_But_With_OpenNow_Should_Display_Open() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(true, null),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(true, null),
                        secondNumber,
                        secondSite
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getOpenRestaurantsViewState(), restaurantsWrapperViewState);

        });

    }

    @Test
    public void without_Periods_List_But_With_ClosedNow_Should_Display_Closed() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(false, null),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(false, null),
                        secondNumber,
                        secondSite
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getClosedRestaurantsViewState(), restaurantsWrapperViewState);

        });

    }

    @Test
    public void restaurant_Closed_Until_More_Than_Tomorrow_Should_Display_Closed_Until_Next_Opening_Day() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "0900"),
                                                new Open(3, "0500")),
                                        new Periods(
                                                new Close(5, "2300"),
                                                new Open(5, "2100"))

                                )
                        ),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "0900"),
                                                new Open(3, "0500")),
                                        new Periods(
                                                new Close(2, "2300"),
                                                new Open(2, "2100"))
                                )
                        ),
                        secondNumber,
                        secondSite
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getClosedUntilNextDayRestaurantsViewState(), restaurantsWrapperViewState);

        });

    }

    @Test
    public void restaurant_Closed_Until_Tomorrow_Should_Display_Closed_Until_Tomorrow() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "0900"),
                                                new Open(3, "0500")),
                                        new Periods(
                                                new Close(4, "2300"),
                                                new Open(4, "2100"))

                                )
                        ),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(3, "0900"),
                                                new Open(3, "0500")),
                                        new Periods(
                                                new Close(4, "2300"),
                                                new Open(4, "2100"))
                                )
                        ),
                        secondNumber,
                        secondSite
                )
        ));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getClosedUntilTomorrowRestaurantsViewState(), restaurantsWrapperViewState);

        });

    }

    @Test
    public void last_Day_Goes_To_Next_Month() {
        // GIVEN
        restaurantsViewModel = new RestaurantsViewModel(
                application,
                locationRepository,
                getNearbySearchResultsUseCase,
                getRestaurantDetailsResultsUseCase,
                usersWhoMadeRestaurantChoiceRepository,
                userSearchRepository,
                clockNewMonth);

        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(0, "0930"),
                                                new Open(0, "0900")),
                                        new Periods(
                                                new Close(1, "2300"),
                                                new Open(1, "1450")

                                        ))
                        ),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(0, "0900"),
                                                new Open(0, "0800")),
                                        new Periods(
                                                new Close(2, "2300"),
                                                new Open(2, "1700")
                                        ))
                        ),
                        secondNumber,
                        secondSite
                )
        ));

        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getLastDayOfMonth(), restaurantsWrapperViewState);

        });

    }

    @Test
    public void last_Day_Goes_To_Next_Year() {
        // GIVEN
        restaurantsViewModel = new RestaurantsViewModel(
                application,
                locationRepository,
                getNearbySearchResultsUseCase,
                getRestaurantDetailsResultsUseCase,
                usersWhoMadeRestaurantChoiceRepository,
                userSearchRepository,
                clockNewYear);

        restaurantDetailsResultsUseCaseMutableLiveData.setValue(getDefaultRestaurantsDetails(
                new RestaurantDetails(
                        firstRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(0, "0930"),
                                                new Open(0, "0900")),
                                        new Periods(
                                                new Close(6, "2300"),
                                                new Open(6, "0830")

                                        ))
                        ),
                        firstNumber,
                        firstSite
                ),
                new RestaurantDetails(
                        secondRestaurantId,
                        new OpeningHours(
                                false,
                                getDefaultPeriod(
                                        new Periods(
                                                new Close(4, "0900"),
                                                new Open(4, "0800")),
                                        new Periods(
                                                new Close(2, "2300"),
                                                new Open(2, "0900")
                                        ))
                        ),
                        secondNumber,
                        secondSite
                )
        ));

        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getLastDayOfYear(), restaurantsWrapperViewState);

        });

    }

    @Test
    public void details_Unavailable_should_Display_Nearby_Results() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(null);
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getNearbyWithoutDetailsRestaurants()));

        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getNearbyWithoutDetails(), restaurantsWrapperViewState);

        });

    }


    ////////// RATING STARS TESTS ///////////

    @Test
    public void get_Rating_Should_Display_Stars() {
        // GIVEN
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getRatingStars()));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getRatingRestaurantsViewState(), restaurantsWrapperViewState);

        });

    }

    /////////// RESTAURANTS LIST TEST ///////////

    @Test
    public void when_Details_List_Is_Empty_Should_Display_Nearby_List() {
        // GIVEN
        restaurantDetailsResultsUseCaseMutableLiveData.setValue(null);
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(RestaurantsViewModelTest.this.getRestaurantWithoutDetailsViewState(), restaurantsWrapperViewState);

        });
    }

    /////////// PHOTO TESTS ///////////

    @Test
    public void when_Photo_List_Is_Null_Should_Display_Photo_Unavailable() {
        // GIVEN
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getNullPhoto()));
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(getNullPhotoViewState(), restaurantsWrapperViewState);

        });

    }

    /////////// SEARCHVIEW AUTOCOMPLETE ///////////

    @Test
    public void when_User_Perform_Search_Should_Display_Result() {
        // GIVEN
        userSearchMutableLiveData.setValue("Second_Restaurant_Name");
        // WHEN
        LiveDataTestUtils.observeForTesting(restaurantsViewModel.getRestaurantsViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(RestaurantsViewModelTest.this.getSearchViewResultViewState(), restaurantsWrapperViewState);

        });


    }

    // VAL FOR TESTING //

    int distanceInt = 0;

    String firstRestaurantId = "First_Restaurant_Id";
    String firstRestaurantName = "First_Restaurant_Name";
    String firstAddress = "First_Address";
    String firstNumber = "First_Phone_Number";
    String firstSite = "First_Website";

    String secondRestaurantId = "Second_Restaurant_Id";
    String secondRestaurantName = "Second_Restaurant_Name";
    String secondAddress = "Second_Address";
    String secondNumber = "Second_Phone_Number";
    String secondSite = "Second_Website";

    String photo = "photo";
    String photoApiUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photo_reference=photo&key=AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";
    String distance = "0m";

    String openH24 = "Open 24/7";
    String open = "Open";
    String closed = "Closed";
    String closingSoon = "Closing soon";
    String permanentlyClosed = "Permanently closed";
    String photoUnavailable = "Photo unavailable";
    String twoUsersChoseThisRestaurant = "(2)";
    String nobodyChoseThisRestaurant = "";
    int textColorGrey = -7829368;
    int textColorBlack = -65536;

    String firstUserId = "First_user_Id";
    String firstUserName = "First_user_name";

    String secondUserId = "Second_user_Id";
    String secondUserName = "Second_user_name";

    // region IN

    private List<Restaurant> getDefaultRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(
                new Restaurant(
                        firstRestaurantId,
                        firstRestaurantName,
                        firstAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(true, null),
                        2,
                        25,
                        false,
                        firstNumber,
                        firstSite

                )
        );
        restaurants.add(
                new Restaurant(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(true, null),
                        4,
                        25,
                        false,
                        secondNumber,
                        secondSite
                )
        );
        return restaurants;

    }

    private List<Restaurant> getPermanentlyClosedRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(
                new Restaurant(
                        firstRestaurantId,
                        firstRestaurantName,
                        firstAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(true, null),
                        2,
                        25,
                        true,
                        firstNumber,
                        firstSite
                )
        );
        restaurants.add(
                new Restaurant(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(true, null),
                        4,
                        25,
                        true,
                        secondNumber,
                        secondSite
                )
        );
        return restaurants;

    }

    private List<Restaurant> getRatingStars() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(
                new Restaurant(
                        firstRestaurantId,
                        firstRestaurantName,
                        firstAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(true, null),
                        4,
                        25,
                        true,
                        firstNumber,
                        firstSite
                )
        );
        restaurants.add(
                new Restaurant(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(true, null),
                        1,
                        25,
                        true,
                        secondNumber,
                        secondSite
                )
        );
        return restaurants;

    }

    private List<Restaurant> getNullPhoto() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(
                new Restaurant(
                        firstRestaurantId,
                        firstRestaurantName,
                        firstAddress,
                        null,
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(true, null),
                        4,
                        25,
                        true,
                        firstNumber,
                        firstSite
                )
        );
        restaurants.add(
                new Restaurant(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondAddress,
                        null,
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(true, null),
                        1,
                        25,
                        true,
                        secondNumber,
                        secondSite
                )
        );
        return restaurants;

    }

    private List<Restaurant> getNearbyWithoutDetailsRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(
                new Restaurant(
                        firstRestaurantId,
                        firstRestaurantName,
                        firstAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(30.0, 42.1)),
                        new OpeningHours(true, null),
                        2,
                        25,
                        false,
                        firstNumber,
                        firstSite
                )
        );
        restaurants.add(
                new Restaurant(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(false, null),
                        4,
                        25,
                        false,
                        secondNumber,
                        secondSite
                )
        );
        return restaurants;

    }

    private List<UserWhoMadeRestaurantChoice> userWhoMadeChoice() {
        List<UserWhoMadeRestaurantChoice> workmateWhoMadeRestaurantChoices = new ArrayList<>();
        workmateWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        firstRestaurantId,
                        firstRestaurantName,
                        firstUserId,
                        firstUserName,
                        firstAddress

                )
        );
        workmateWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        firstRestaurantId,
                        firstRestaurantName,
                        secondUserId,
                        secondUserName,
                        firstAddress

                )
        );
        return workmateWhoMadeRestaurantChoices;
    }

    private List<Photo> getPhoto() {
        return Collections.singletonList(
                new Photo(
                        400,
                        400,
                        new ArrayList<>(),
                        photo

                )
        );
    }


    private List<RestaurantDetailsResult> getDefaultRestaurantsDetails(RestaurantDetails... restaurantDetails) {

        List<RestaurantDetailsResult> results = new ArrayList<>();

        for (RestaurantDetails restaurantDetail : restaurantDetails) {
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

    // endregion

    /////////////////////////////////////////////////////////

    // region OUT

    private RestaurantsWrapperViewState getDefaultRestaurantViewState() {
        return new RestaurantsWrapperViewState(getRestaurantsResults());

    }

    private List<RestaurantsViewState> getRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                "Open until 1.pm",
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                "Open until 11:50am",
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getRestaurantsClosingSoon() {
        return new RestaurantsWrapperViewState(getRestaurantsClosingSoonResults());

    }

    private List<RestaurantsViewState> getRestaurantsClosingSoonResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                closingSoon,
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorBlack),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                closingSoon,
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorBlack)

        );
    }

    private RestaurantsWrapperViewState getRestaurantsClosed() {
        return new RestaurantsWrapperViewState(getRestaurantsClosedResults());

    }

    private List<RestaurantsViewState> getRestaurantsClosedResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                "Closed until 6.pm",
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                "Closed until 11.am",
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }


    private RestaurantsWrapperViewState getPermanentlyOpenRestaurant() {
        return new RestaurantsWrapperViewState(getPermanentlyOpenRestaurantsResults());

    }

    private List<RestaurantsViewState> getPermanentlyOpenRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                openH24,
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                openH24,
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getPermanentlyClosedRestaurantsViewState() {
        return new RestaurantsWrapperViewState(getPermanentlyClosedRestaurantsResults());

    }

    private List<RestaurantsViewState> getPermanentlyClosedRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                permanentlyClosed,
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                permanentlyClosed,
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getOpenRestaurantsViewState() {
        return new RestaurantsWrapperViewState(getOpenNowRestaurantsResults());

    }

    private List<RestaurantsViewState> getOpenNowRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                open,
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                open,
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getClosedRestaurantsViewState() {
        return new RestaurantsWrapperViewState(getClosedNowRestaurantsResults());

    }

    private List<RestaurantsViewState> getClosedNowRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                closed,
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                closed,
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getClosedUntilNextDayRestaurantsViewState() {
        return new RestaurantsWrapperViewState(getClosedUntilNextDayRestaurantsResults());

    }

    private List<RestaurantsViewState> getClosedUntilNextDayRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Friday 9.pm",
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Tuesday 9.pm",
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getClosedUntilTomorrowRestaurantsViewState() {
        return new RestaurantsWrapperViewState(getClosedUntilTomorrowRestaurantsResults());

    }

    private List<RestaurantsViewState> getClosedUntilTomorrowRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Tomorrow 9.pm",
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Tomorrow 9.pm",
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getRatingRestaurantsViewState() {
        return new RestaurantsWrapperViewState(getRatingRestaurantsResults());

    }

    private List<RestaurantsViewState> getRatingRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                permanentlyClosed,
                                2,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                permanentlyClosed,
                                1,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getRestaurantWithoutDetailsViewState() {
        return new RestaurantsWrapperViewState(getRestaurantsWithoutDetails());

    }

    private List<RestaurantsViewState> getRestaurantsWithoutDetails() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                open,
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                open,
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getNullPhotoViewState() {
        return new RestaurantsWrapperViewState(getNullPhotoRestaurantsResults());

    }

    private List<RestaurantsViewState> getNullPhotoRestaurantsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoUnavailable,
                                distance,
                                permanentlyClosed,
                                2,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoUnavailable,
                                distance,
                                permanentlyClosed,
                                1,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getLastDayOfMonth() {
        return new RestaurantsWrapperViewState(getLastDayOfMonthResults());

    }

    private List<RestaurantsViewState> getLastDayOfMonthResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Monday 2:50pm",
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Tuesday 5.pm",
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getLastDayOfYear() {
        return new RestaurantsWrapperViewState(getLastDayOfYearResults());

    }

    private List<RestaurantsViewState> getLastDayOfYearResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Tomorrow 8:30am",
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                "Closed until Tuesday 9.am",
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getNearbyWithoutDetails() {
        return new RestaurantsWrapperViewState(getNearbyWithoutDetailsResults());

    }

    private List<RestaurantsViewState> getNearbyWithoutDetailsResults() {
        return Arrays.asList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                firstRestaurantName,
                                firstAddress,
                                photoApiUrl,
                                distance,
                                open,
                                1,
                                firstRestaurantId,
                                twoUsersChoseThisRestaurant,
                                textColorGrey),

                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                closed,
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }

    private RestaurantsWrapperViewState getSearchViewResultViewState() {
        return new RestaurantsWrapperViewState(getSearchViewResult());

    }

    private List<RestaurantsViewState> getSearchViewResult() {
        return Collections.singletonList(
                new RestaurantsViewState
                        (
                                distanceInt,
                                secondRestaurantName,
                                secondAddress,
                                photoApiUrl,
                                distance,
                                "Open until 11:50am",
                                2,
                                secondRestaurantId,
                                nobodyChoseThisRestaurant,
                                textColorGrey)

        );
    }


    // endregion

}
