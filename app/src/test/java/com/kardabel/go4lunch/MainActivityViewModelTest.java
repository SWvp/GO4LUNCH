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

    private String currentUserId = "current_User_Id";
    private String userTypeText = "user_Text";

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
    public void type_Text_In_Search_View() {
        // WHEN
        mainActivityViewModel.sendTextToAutocomplete("firs");
        LiveDataTestUtils.observeForTesting(mainActivityViewModel.getPredictionsLiveData(), new LiveDataTestUtils.OnObservedListener<List<PredictionViewState>>() {
            @Override
            public void onObserved(List<PredictionViewState> predictions) {
                assertEquals(MainActivityViewModelTest.this.getDefaultPredictionsViewState(), predictions);


            }
        });


    }



    // VAL FOR TESTING //
    String firstRestaurantId = "First_Restaurant_Id";
    String firstRestaurantName = "First_Restaurant_Name";

    String secondRestaurantId = "Second_Restaurant_Id";
    String secondRestaurantName = "Second_Restaurant_Name";

    String secondUserId = "Second_user_Id";

    // region In

    private Predictions getDefaultPrediction() {
        List<Prediction> prediction = new ArrayList<>();
        prediction.add(new Prediction(
                        "first_description",
                        "first_retaurant_Id",
                        new PlaceAutocompleteStructuredFormat(
                                "first_name"
                        )
                )
        );
        prediction.add(new Prediction(
                        "second_description",
                        "second_restaurant_Id",
                        new PlaceAutocompleteStructuredFormat(
                                "second_name"
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
                        currentUserId

                )
        );
        workmateWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        secondRestaurantId,
                        secondRestaurantName,
                        secondUserId

                )
        );
        return workmateWhoMadeRestaurantChoices;
    }

    // endregion

    // region Out

    private List<PredictionViewState> getDefaultPredictionsViewState() {
        List<PredictionViewState> predictions = new ArrayList<>();
        predictions.add(new PredictionViewState(
                firstRestaurantId,
                firstRestaurantId,
                firstRestaurantId
        ));
        return predictions;
    }

    // endregion

}
