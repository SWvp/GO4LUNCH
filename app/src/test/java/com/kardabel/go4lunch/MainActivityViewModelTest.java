package com.kardabel.go4lunch;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.pojo.PlaceAutocompleteStructuredFormat;
import com.kardabel.go4lunch.pojo.Prediction;
import com.kardabel.go4lunch.pojo.Predictions;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.UserSearchRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.ui.autocomplete.PredictionViewState;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;
import com.kardabel.go4lunch.usecase.GetPredictionsUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModelTest {

    private final String currentUserId = "current_User_Id";
    private final String userTypeText = "first_name";

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    // MOCK THE INJECTIONS
    private final Application application =
            Mockito.mock(Application.class);
    private final LocationRepository locationRepository =
            Mockito.mock(LocationRepository.class);
    private final GetPredictionsUseCase getPredictionsUseCase =
            Mockito.mock(GetPredictionsUseCase.class);
    private final UserSearchRepository userSearchRepository =
            Mockito.mock(UserSearchRepository.class);
    private final UsersWhoMadeRestaurantChoiceRepository usersWhoMadeRestaurantChoiceRepository =
            Mockito.mock(UsersWhoMadeRestaurantChoiceRepository.class);
    private final GetCurrentUserIdUseCase getCurrentUserIdUseCase =
            Mockito.mock(GetCurrentUserIdUseCase.class);

    // CREATE MUTABLE
    private final MutableLiveData<Location> locationMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<Predictions> predictionsMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<String> userSearchMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<UserWhoMadeRestaurantChoice>> userWhoMadeRestaurantChoiceMutableLiveData =
            new MutableLiveData<>();

    private final Location location =
            Mockito.mock(Location.class);

    private MainActivityViewModel mainActivityViewModel;

    @Before
    public void setUp() {

        // STRINGS RETURNS
        Mockito.doReturn("No current user restaurant choice").when(application).getString(R.string.no_current_user_restaurant_choice);

        // RETURNS OF MOCKED CLASS
        Mockito.doReturn(locationMutableLiveData)
                .when(locationRepository)
                .getLocationLiveData();
        Mockito.doReturn(predictionsMutableLiveData)
                .when(getPredictionsUseCase)
                .invoke(userTypeText);
        Mockito.doReturn(userSearchMutableLiveData)
                .when(userSearchRepository)
                .getUsersSearchLiveData();
        Mockito.doReturn(userWhoMadeRestaurantChoiceMutableLiveData)
                .when(usersWhoMadeRestaurantChoiceRepository)
                .getWorkmatesWhoMadeRestaurantChoice();
        Mockito.doReturn(currentUserId)
                .when(getCurrentUserIdUseCase)
                .invoke();

        // SET LIVEDATA VALUES
        locationMutableLiveData.setValue(location);
        predictionsMutableLiveData.setValue(getDefaultPrediction());
        userSearchMutableLiveData.setValue(null);
        userWhoMadeRestaurantChoiceMutableLiveData.setValue(userWhoMadeChoice());

        mainActivityViewModel = new MainActivityViewModel(
                application,
                locationRepository,
                getPredictionsUseCase,
                userSearchRepository,
                usersWhoMadeRestaurantChoiceRepository,
                getCurrentUserIdUseCase
        );

    }

    @Test
    public void type_Text_In_Search_View_Should_Display_Predictions() {
        // WHEN
        mainActivityViewModel.sendTextToAutocomplete(userTypeText);
        LiveDataTestUtils.observeForTesting(mainActivityViewModel.getPredictionsLiveData(), predictions -> {
            // THEN
            assertEquals(MainActivityViewModelTest.this.getDefaultPredictionsViewState(), predictions);

        });
    }

    @Test
    public void user_choice() {
        // WHEN
        mainActivityViewModel.getUserRestaurantChoice();
        LiveDataTestUtils.observeForTesting(mainActivityViewModel.getCurrentUserRestaurantChoice(), yourLunch -> {
            // THEN
            assertEquals(MainActivityViewModelTest.this.getDefaultYourLunchViewState(), yourLunch);

        });
    }

    // VAL FOR TESTING //
    String firstDescription = "first_description";
    String firstRestaurantId = "First_Restaurant_Id";
    String firstRestaurantName = "First_Restaurant_Name";
    String firstRestaurantAddress = "First_Restaurant_Address";
    String nameOne = "name_one";

    String secondDescription = "second_description";
    String secondRestaurantId = "Second_Restaurant_Id";
    String secondRestaurantName = "Second_Restaurant_Name";
    String secondRestaurantAddress = "Second_Restaurant_Address";
    String nameTwo = "name_two";

    String currentUserName = "Current_user_name";

    String secondUserId = "Second_user_Id";
    String secondUserName = "Second_user_name";


    // region In

    private Predictions getDefaultPrediction() {
        List<Prediction> prediction = new ArrayList<>();
        prediction.add(new Prediction(
                firstDescription,
                firstRestaurantId,
                        new PlaceAutocompleteStructuredFormat(
                                nameOne
                        )
                )
        );
        prediction.add(new Prediction(
                secondDescription,
                secondRestaurantId,
                        new PlaceAutocompleteStructuredFormat(
                                nameTwo
                        )
                )
        );
        return new Predictions(
                prediction,
                "status");
    }

    private List<UserWhoMadeRestaurantChoice> userWhoMadeChoice() {
        List<UserWhoMadeRestaurantChoice> workmateWhoMadeRestaurantChoices = new ArrayList<>();
        workmateWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        firstRestaurantId,
                        firstRestaurantName,
                        currentUserId,
                        currentUserName,
                        firstRestaurantAddress


                )
        );
        workmateWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondUserId,
                        secondUserName,
                        secondRestaurantAddress

                )
        );
        return workmateWhoMadeRestaurantChoices;
    }

    // endregion

    // region Out

    private List<PredictionViewState> getDefaultPredictionsViewState() {
        List<PredictionViewState> predictions = new ArrayList<>();
        predictions.add(new PredictionViewState(
                firstDescription,
                firstRestaurantId,
                nameOne
        ));
        predictions.add(new PredictionViewState(
                secondDescription,
                secondRestaurantId,
                nameTwo
        ));
        return predictions;
    }

    private MainActivityYourLunchViewState getDefaultYourLunchViewState() {

        return new MainActivityYourLunchViewState(
                firstRestaurantId,
                1
        );
    }

    // endregion

}
