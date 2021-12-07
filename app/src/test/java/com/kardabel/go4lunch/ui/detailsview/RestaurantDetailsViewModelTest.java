package com.kardabel.go4lunch.ui.detailsview;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.pojo.Close;
import com.kardabel.go4lunch.pojo.FavoriteRestaurant;
import com.kardabel.go4lunch.pojo.Geometry;
import com.kardabel.go4lunch.pojo.Open;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Periods;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.Restaurant;
import com.kardabel.go4lunch.pojo.RestaurantDetails;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.RestaurantLatLngLiteral;
import com.kardabel.go4lunch.repository.FavoriteRestaurantsRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsByIdUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsByIdUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantDetailsViewModelTest {
    private final String firstRestaurantId = "First_Restaurant_Id";

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final Application application =
            Mockito.mock(Application.class);
    private final GetNearbySearchResultsByIdUseCase getNearbySearchResultsByIdUseCase =
            Mockito.mock(GetNearbySearchResultsByIdUseCase.class);
    private final GetRestaurantDetailsResultsByIdUseCase getRestaurantDetailsResultsByIdUseCase =
            Mockito.mock(GetRestaurantDetailsResultsByIdUseCase.class);
    private final UsersWhoMadeRestaurantChoiceRepository usersWhoMadeRestaurantChoiceRepository =
            Mockito.mock(UsersWhoMadeRestaurantChoiceRepository.class);
    private final WorkmatesRepository workmatesRepository =
            Mockito.mock(WorkmatesRepository.class);
    private final FavoriteRestaurantsRepository favoriteRestaurantsRepository =
            Mockito.mock(FavoriteRestaurantsRepository.class);
    private final GetCurrentUserIdUseCase getCurrentUserIdUseCase =
            Mockito.mock(GetCurrentUserIdUseCase.class);

    private final MutableLiveData<Restaurant> restaurantLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<RestaurantDetailsResult> restaurantDetailsLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<UserWhoMadeRestaurantChoice>> usersWhoMadeRestaurantChoiceLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<UserModel>> workMatesLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<FavoriteRestaurant>> favoriteRestaurantsLiveData =
            new MutableLiveData<>();

    private RestaurantDetailsViewModel restaurantDetailsViewModel;


    @Before
    public void setUp() {

        // STRINGS RETURNS
        Mockito.doReturn("Phone number unavailable").when(application).getString(R.string.phone_number_unavailable);
        Mockito.doReturn("https://www.google.com/").when(application).getString(R.string.website_unavailable);
        Mockito.doReturn("is joining !").when(application).getString(R.string.is_joining);
        Mockito.doReturn("https://maps.googleapis.com/maps/api/place/").when(application).getString(R.string.api_url);
        Mockito.doReturn("photo?maxwidth=300&photo_reference=").when(application).getString(R.string.photo_reference);
        Mockito.doReturn("&key=").when(application).getString(R.string.and_key);
        Mockito.doReturn("AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8").when(application).getString(R.string.google_map_key);
        Mockito.doReturn("Photo unavailable").when(application).getString(R.string.photo_unavailable);

        // RETURNS OF MOCKED CLASS
        Mockito.doReturn(restaurantLiveData)
                .when(getNearbySearchResultsByIdUseCase)
                .invoke(firstRestaurantId);
        Mockito.doReturn(restaurantDetailsLiveData)
                .when(getRestaurantDetailsResultsByIdUseCase)
                .invoke(firstRestaurantId);
        Mockito.doReturn(usersWhoMadeRestaurantChoiceLiveData)
                .when(usersWhoMadeRestaurantChoiceRepository)
                .getWorkmatesWhoMadeRestaurantChoice();
        Mockito.doReturn(workMatesLiveData)
                .when(workmatesRepository)
                .getWorkmates();
        Mockito.doReturn(favoriteRestaurantsLiveData)
                .when(favoriteRestaurantsRepository)
                .getFavoriteRestaurants();
        String getCurrentUserId = "currentUserId";
        Mockito.doReturn(getCurrentUserId)
                .when(getCurrentUserIdUseCase)
                .invoke();

        // SET LIVEDATA VALUES
        restaurantLiveData.setValue(getDefaultRestaurant());
        restaurantDetailsLiveData.setValue(new RestaurantDetailsResult(restaurantDetails()));
        usersWhoMadeRestaurantChoiceLiveData.setValue(workmatesWhoMadeChoice());
        workMatesLiveData.setValue(getDefaultWorkmates());
        favoriteRestaurantsLiveData.setValue(getDefaultFavoriteRestaurants());


        restaurantDetailsViewModel = new RestaurantDetailsViewModel(
                application,
                getNearbySearchResultsByIdUseCase,
                getRestaurantDetailsResultsByIdUseCase,
                usersWhoMadeRestaurantChoiceRepository,
                workmatesRepository,
                favoriteRestaurantsRepository,
                getCurrentUserIdUseCase
        );
    }

    @Test
    public void retrieveRestaurantDetails() {
        // WHEN
        restaurantDetailsViewModel.init(firstRestaurantId);
        LiveDataTestUtils.observeForTesting(restaurantDetailsViewModel.getRestaurantDetailsViewStateLiveData(), restaurantDetails -> {
            // THEN
            assertEquals(RestaurantDetailsViewModelTest.this.getDefaultRestaurantDetailsViewState(), restaurantDetails);

            verify(getNearbySearchResultsByIdUseCase).invoke(firstRestaurantId);
            verify(getRestaurantDetailsResultsByIdUseCase).invoke(firstRestaurantId);
            verify(usersWhoMadeRestaurantChoiceRepository).getWorkmatesWhoMadeRestaurantChoice();
            verify(workmatesRepository).getWorkmates();
            verify(favoriteRestaurantsRepository).getFavoriteRestaurants();
            verifyNoMoreInteractions(
                    getNearbySearchResultsByIdUseCase,
                    getRestaurantDetailsResultsByIdUseCase,
                    usersWhoMadeRestaurantChoiceRepository,
                    workmatesRepository,
                    favoriteRestaurantsRepository);

        });
    }

    @Test
    public void retrieveWorkmate() {
        // WHEN
        restaurantDetailsViewModel.init(firstRestaurantId);
        LiveDataTestUtils.observeForTesting(restaurantDetailsViewModel.getWorkmatesWhoChoseThisRestaurant(), workmates -> {
            // THEN
            assertEquals(RestaurantDetailsViewModelTest.this.getDefaultWorkmatesViewState(), workmates);

            verify(getNearbySearchResultsByIdUseCase).invoke(firstRestaurantId);
            verify(getRestaurantDetailsResultsByIdUseCase).invoke(firstRestaurantId);
            verify(usersWhoMadeRestaurantChoiceRepository).getWorkmatesWhoMadeRestaurantChoice();
            verify(workmatesRepository).getWorkmates();
            verify(favoriteRestaurantsRepository).getFavoriteRestaurants();
            verifyNoMoreInteractions(
                    getNearbySearchResultsByIdUseCase,
                    getRestaurantDetailsResultsByIdUseCase,
                    usersWhoMadeRestaurantChoiceRepository,
                    workmatesRepository,
                    favoriteRestaurantsRepository);

        });
    }


    // region IN

    // VAL FOR TESTING
    String firstRestaurantName = "First_Restaurant_Name";
    String firstAddress = "First_Restaurant_Address";
    String firstNumber = "First_Phone_Number";
    String firstSite = "First_Website";

    String secondRestaurantName = "Second_Restaurant_Name";
    String secondRestaurantId = "Second_Restaurant_Id";

    String firstUserId = "First_User_Id";
    String firstUserName = "First_Name";
    String firstAvatar = "First_Avatar";
    String firstEmail = "First_Email";

    String secondUserId = "Second_User_Id";
    String secondUserName = "Second_Name";
    String secondAvatar = "Second_Avatar";
    String secondEmail = "Second_Email";

    String thirdUserId = "Third_User_Id";
    String thirdUserName = "Third_Name";
    String thirdAvatar = "Third_Avatar";
    String thirdEmail = "Third_Email";

    String fourthUserId = "Fourth_User_Id";
    String fourthUserName = "Fourth_Name";
    String fourthAvatar = "Fourth_Avatar";
    String fourthEmail = "Fourth_Email";

    String photo = "photo";
    String photoApiUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photo_reference=photo&key=AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";

    private Restaurant getDefaultRestaurant() {
        return new Restaurant(
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

        );
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

    private List<Periods> getDefaultPeriod(Periods... periods) {
        List<Periods> results = new ArrayList<>();

        for (Periods period : periods) {
            results.add(new Periods(
                    period.getClose(),
                    period.getOpen()));

        }
        return results;

    }

    private RestaurantDetails restaurantDetails() {
        return new RestaurantDetails(
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
        );
    }

    private List<UserWhoMadeRestaurantChoice> workmatesWhoMadeChoice() {
        List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices = new ArrayList<>();
        userWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        firstRestaurantId,
                        firstRestaurantName,
                        thirdUserId

                )
        );
        userWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        secondRestaurantId,
                        secondRestaurantName,
                        fourthUserId

                )
        );
        return userWhoMadeRestaurantChoices;
    }

    private List<UserModel> getDefaultWorkmates() {
        List<UserModel> workmates = new ArrayList<>();
        workmates.add(
                new UserModel(
                        firstUserId,
                        firstUserName,
                        firstAvatar,
                        firstEmail
                )
        );
        workmates.add(
                new UserModel(
                        secondUserId,
                        secondUserName,
                        secondAvatar,
                        secondEmail
                )
        );
        workmates.add(
                new UserModel(
                        thirdUserId,
                        thirdUserName,
                        thirdAvatar,
                        thirdEmail
                )
        );
        workmates.add(
                new UserModel(
                        fourthUserId,
                        fourthUserName,
                        fourthAvatar,
                        fourthEmail
                )
        );
        return workmates;
    }

    private List<FavoriteRestaurant> getDefaultFavoriteRestaurants() {

        List<FavoriteRestaurant> favoriteRestaurants = new ArrayList<>();

        favoriteRestaurants.add(new FavoriteRestaurant(
                firstRestaurantId,
                firstRestaurantName

        ));
        favoriteRestaurants.add(new FavoriteRestaurant(
                secondRestaurantId,
                secondRestaurantName

        ));

        return favoriteRestaurants;
    }

    // endregion

    /////////////////////////////////////////////////////////

    // region OUT

    private RestaurantDetailsViewState getDefaultRestaurantDetailsViewState() {
        return new RestaurantDetailsViewState(
                firstRestaurantName,
                firstAddress,
                photoApiUrl,
                firstNumber,
                firstSite,
                firstRestaurantId,
                1,
                2131165351,
                2131165335
        );
    }

    private List<RestaurantDetailsWorkmatesViewState> getDefaultWorkmatesViewState() {
        List<RestaurantDetailsWorkmatesViewState> workMates = new ArrayList<>();
        workMates.add(new RestaurantDetailsWorkmatesViewState(
                thirdUserName + " is joining !",
                thirdAvatar
        ));

        return workMates;

    }
}
