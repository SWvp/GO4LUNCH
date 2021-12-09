package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.pojo.Prediction;
import com.kardabel.go4lunch.pojo.Predictions;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.UserSearchRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.ui.autocomplete.PredictionViewState;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;
import com.kardabel.go4lunch.usecase.GetPredictionsUseCase;
import com.kardabel.go4lunch.util.PermissionsViewAction;
import com.kardabel.go4lunch.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final Application application;

    private final LocationRepository locationRepository;
    private final GetPredictionsUseCase getPredictionsUseCase;
    private final UserSearchRepository userSearchRepository;
    private final UsersWhoMadeRestaurantChoiceRepository usersWhoMadeRestaurantChoiceRepository;
    private final GetCurrentUserIdUseCase getCurrentUserIdUseCase;

    private final MediatorLiveData<List<PredictionViewState>> predictionsMediatorLiveData =
            new MediatorLiveData<>();
    private final MediatorLiveData<MainActivityYourLunchViewState> mainActivityYourLunchViewStateMediatorLiveData =
            new MediatorLiveData<>();

    private final SingleLiveEvent<PermissionsViewAction> actionSingleLiveEvent =
            new SingleLiveEvent<>();



    public MainActivityViewModel(
            @NonNull Application application,
            @NonNull LocationRepository locationRepository,
            @NonNull GetPredictionsUseCase getPredictionsUseCase,
            @NonNull UserSearchRepository userSearchRepository,
            @NonNull UsersWhoMadeRestaurantChoiceRepository usersWhoMadeRestaurantChoiceRepository,
            @NonNull GetCurrentUserIdUseCase getCurrentUserIdUseCase) {

        super();
        this.application = application;
        this.locationRepository = locationRepository;
        this.getPredictionsUseCase = getPredictionsUseCase;
        this.userSearchRepository = userSearchRepository;
        this.usersWhoMadeRestaurantChoiceRepository = usersWhoMadeRestaurantChoiceRepository;
        this.getCurrentUserIdUseCase = getCurrentUserIdUseCase;

    }

    // CHECK PERMISSIONS
    public void checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(application, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)) {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_DENIED);
        } else {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_ASKED);

        }
    }

    // WHEN PERMISSION IS GRANTED, RETRIEVE USER LOCATION
    private void permissionGranted() {
        locationRepository.StartLocationRequest();

    }

    // WHEN CLICKING ON SEARCH VIEW WE PASSED THE TEXT TO USE CASE AND THEN OBSERVE IT
    public void sendTextToAutocomplete(String text) {
        LiveData<Predictions> predictionsLiveData = getPredictionsUseCase.invoke(text);
        predictionsMediatorLiveData.addSource(predictionsLiveData, this::combine);

    }

    private void combine(Predictions predictions) {
        if (predictions != null) {
            predictionsMediatorLiveData.setValue(map(predictions));
        }
    }

    // MAP THE PREDICTIONS RESULT TO VIEW STATE
    private List<PredictionViewState> map(Predictions predictions) {

        List<PredictionViewState> predictionsList = new ArrayList<>();

        for (Prediction prediction : predictions.getPredictions()) {
            predictionsList.add(new PredictionViewState(
                    prediction.getDescription(),
                    prediction.getPlaceId(),
                    prediction.getStructuredFormatting().getName()
            ));
        }

        return predictionsList;

    }

    // RETRIEVE THE CURRENT USER RESTAURANT CHOICE
    public void getUserRestaurantChoice() {
        LiveData<List<UserWhoMadeRestaurantChoice>> workmatesWhoMadeRestaurantChoiceLiveData =
                usersWhoMadeRestaurantChoiceRepository.getWorkmatesWhoMadeRestaurantChoice();

        mainActivityYourLunchViewStateMediatorLiveData.addSource(
                workmatesWhoMadeRestaurantChoiceLiveData, this::mapUserRestaurantChoice);
    }

    private void mapUserRestaurantChoice(List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices) {

        String currentUserId = getCurrentUserIdUseCase.invoke();
        MainActivityYourLunchViewState yourLunch = new MainActivityYourLunchViewState(
                application.getString(R.string.no_current_user_restaurant_choice),
                0
        );

        for (UserWhoMadeRestaurantChoice workmate : userWhoMadeRestaurantChoices) {
            if (workmate.getUserId().equals(currentUserId)) {
                yourLunch = new MainActivityYourLunchViewState(
                        workmate.getRestaurantId(),
                        1
                );

            }
        }
        mainActivityYourLunchViewStateMediatorLiveData.setValue(yourLunch);
    }

    public void userSearch(String predictionText) {
        userSearchRepository.usersSearch(predictionText);

    }

    public SingleLiveEvent<PermissionsViewAction> getActionSingleLiveEvent() {
        return actionSingleLiveEvent;

    }

    public LiveData<MainActivityYourLunchViewState> getCurrentUserRestaurantChoice() {
        return mainActivityYourLunchViewStateMediatorLiveData;

    }

    public LiveData<List<PredictionViewState>> getPredictionsLiveData() {
        return predictionsMediatorLiveData;

    }

}
