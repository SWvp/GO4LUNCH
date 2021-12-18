package com.kardabel.go4lunch.ui.mapview;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.pojo.Geometry;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantLatLngLiteral;
import com.kardabel.go4lunch.pojo.Restaurant;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.UserSearchRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;

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

    private final LocationRepository locationRepository =
            Mockito.mock(LocationRepository.class);
    private final GetNearbySearchResultsUseCase getNearbySearchResultsUseCase =
            Mockito.mock(GetNearbySearchResultsUseCase.class);
    private final UsersWhoMadeRestaurantChoiceRepository mUsersWhoMadeRestaurantChoiceRepository =
            Mockito.mock(UsersWhoMadeRestaurantChoiceRepository.class);
    private final UserSearchRepository mUserSearchRepository =
            Mockito.mock(UserSearchRepository.class);

    private final Location location = Mockito.mock(Location.class);

    private final MutableLiveData<Location> locationMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<NearbySearchResults> nearbySearchResultsMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<UserWhoMadeRestaurantChoice>> workmatesWhoMadeRestaurantChoiceMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<String> usersSearchMutableLiveData =
            new MutableLiveData<>();

    private MapViewModel mapViewModel;

    @Before
    public void setUp() {
        // SETUP THE LOCATION VALUE
        Mockito.doReturn(EXPECTED_LATITUDE)
                .when(location)
                .getLatitude();
        Mockito.doReturn(EXPECTED_LONGITUDE)
                .when(location)
                .getLongitude();

        // RETURNS OF MOCKED CLASS
        Mockito.doReturn(locationMutableLiveData)
                .when(locationRepository)
                .getLocationLiveData();
        Mockito.doReturn(nearbySearchResultsMutableLiveData)
                .when(getNearbySearchResultsUseCase)
                .invoke();
        Mockito.doReturn(workmatesWhoMadeRestaurantChoiceMutableLiveData)
                .when(mUsersWhoMadeRestaurantChoiceRepository)
                .getWorkmatesWhoMadeRestaurantChoice();
        Mockito.doReturn(usersSearchMutableLiveData)
                .when(mUserSearchRepository)
                .getUsersSearchLiveData();


        // SET LIVEDATA VALUES
        locationMutableLiveData.setValue(location);
        nearbySearchResultsMutableLiveData.setValue(new NearbySearchResults(getDefaultRestaurants()));
        workmatesWhoMadeRestaurantChoiceMutableLiveData.setValue(workmatesWhoMadeChoice());
        usersSearchMutableLiveData.setValue(null);

        mapViewModel = new MapViewModel(
                locationRepository,
                getNearbySearchResultsUseCase,
                mUsersWhoMadeRestaurantChoiceRepository,
                mUserSearchRepository);
    }

    @Test
    public void nominalCase() {
        // WHEN
        LiveDataTestUtils.observeForTesting(mapViewModel.getMapViewStateLiveData(), mapViewState -> {
            // THEN
            assertEquals(MapViewModelTest.this.getDefaultMapViewState(), mapViewState);

            verify(locationRepository).getLocationLiveData();
            verify(getNearbySearchResultsUseCase).invoke();
            verify(mUsersWhoMadeRestaurantChoiceRepository).getWorkmatesWhoMadeRestaurantChoice();
            verify(mUserSearchRepository).getUsersSearchLiveData();
            verifyNoMoreInteractions(
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    mUserSearchRepository);
        });
    }

    /////////// SEARCHVIEW AUTOCOMPLETE ///////////

    @Test
    public void when_User_Perform_Search_Should_Display_Result() {
        // GIVEN
        usersSearchMutableLiveData.setValue("Second_Restaurant_Name");
        // WHEN
        LiveDataTestUtils.observeForTesting(mapViewModel.getMapViewStateLiveData(), restaurantsWrapperViewState -> {
            // THEN
            assertEquals(MapViewModelTest.this.getUserSearchMapViewState(), restaurantsWrapperViewState);

        });


    }

    // VAL FOR TESTING //

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

    String thirdRestaurantId = "Third_Restaurant_Id";
    String thirdRestaurantName = "Third_Restaurant_Name";
    String thirdAddress = "Third_Address";
    String thirdNumber = "Third_Phone_Number";
    String thirdSite = "Third_Website";

    String photoReference = "photo";

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
                        new OpeningHours(false, null),
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
                        2,
                        25,
                        false,
                        secondNumber,
                        secondSite
                )
        );
        restaurants.add(
                new Restaurant(
                        thirdRestaurantId,
                        thirdRestaurantName,
                        thirdAddress,
                        getPhoto(),
                        new Geometry(new RestaurantLatLngLiteral(32.1, 42.2)),
                        new OpeningHours(false, null),
                        2,
                        25,
                        false,
                        thirdNumber,
                        thirdSite
                )
        );
        return restaurants;

    }

    private List<UserWhoMadeRestaurantChoice> workmatesWhoMadeChoice() {
        List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices = new ArrayList<>();
        userWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        firstRestaurantId,
                        firstRestaurantName,
                        firstUserId,
                        firstUserName,
                        firstAddress

                )
        );
        userWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondUserId,
                        secondUserName,
                        secondAddress

                )
        );
        return userWhoMadeRestaurantChoices;
    }

    private List<Photo> getPhoto() {

        return Collections.singletonList(
                new Photo(
                        400,
                        400,
                        new ArrayList<>(),
                        photoReference

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
                firstRestaurantName,
                firstRestaurantId,
                firstAddress,
                new LatLng(30.0, 42.1),
                true
        ));
        poi.add(new Poi(
                secondRestaurantName,
                secondRestaurantId,
                secondAddress,
                new LatLng(32.1, 42.2),
                true
        ));
        poi.add(new Poi(
                thirdRestaurantName,
                thirdRestaurantId,
                thirdAddress,
                new LatLng(32.1, 42.2),
                false
        ));

        return poi;
    }

    private MapViewState getUserSearchMapViewState() {
        return new MapViewState(
                getPoiUserSearch(),
                new LatLng(EXPECTED_LATITUDE, EXPECTED_LONGITUDE), EXPECTED_ZOOM_FOCUS);
    }

    private List<Poi> getPoiUserSearch() {
        List<Poi> poi = new ArrayList<>();
             poi.add(new Poi(
                secondRestaurantName,
                secondRestaurantId,
                secondAddress,
                new LatLng(32.1, 42.2),
                true
        ));
        return poi;
    }


    // endregion OUT

}
