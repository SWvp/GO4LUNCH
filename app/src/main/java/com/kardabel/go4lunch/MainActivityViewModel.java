package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.Prediction;
import com.kardabel.go4lunch.pojo.Predictions;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.UsersSearchRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.usecase.GetPredictionsUseCase;
import com.kardabel.go4lunch.util.PermissionsViewAction;
import com.kardabel.go4lunch.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final MediatorLiveData<List<PredictionsViewState>> predictionsMediatorLiveData = new MediatorLiveData<>();

    private final SingleLiveEvent<PermissionsViewAction> actionSingleLiveEvent = new SingleLiveEvent<>();

    private final Application application;

    private final LocationRepository locationRepository;
    private final WorkmatesRepository workmatesRepository;
    private final GetPredictionsUseCase getPredictionsUseCase;
    private final UsersSearchRepository usersSearchRepository;


    public MainActivityViewModel(
            @NonNull Application application,
            @NonNull LocationRepository locationRepository,
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull GetPredictionsUseCase getPredictionsUseCase,
            @NonNull UsersSearchRepository usersSearchRepository


            ) {
        super();
        this.application = application;
        this.locationRepository = locationRepository;
        this.workmatesRepository = workmatesRepository;
        this.getPredictionsUseCase = getPredictionsUseCase;
        this.usersSearchRepository = usersSearchRepository;


    }

    // CHECK PERMISSIONS
    public void checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(application, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_GRANTED);
            permissionGranted();

        }
  //    else if(ContextCompat.checkSelfPermission(application, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
  //        actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_ASKED);

  //    }

 //  else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)) {
 //      actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_DENIED);

 //  }
        else {
            actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_ASKED);

        }
    }

    // WHEN PERMISSION IS GRANTED, LETS RETRIEVE LOCATION AND USER DATA BASE
    private void permissionGranted() {
        locationRepository.StartLocationRequest();
        workmatesRepository.getWorkmates();
        workmatesRepository.getWorkmatesWithFavoriteRestaurant();

    }

    // WHEN CLICKING ON SEARCHVIEW
    public void retrievePredictions(String text) {


            LiveData<Predictions> predictionsLiveData = getPredictionsUseCase.invoke(text);

            // TODO: observe and retrieve the distance to calculate the distance from user

            predictionsMediatorLiveData.addSource(predictionsLiveData, predictions -> combine(predictions));


    }

    private void combine(Predictions predictions) {
        if(predictions!= null){
            predictionsMediatorLiveData.setValue(map(predictions));
        }
    }

    // MAP THE PREDICTIONS RESULT TO VIEW STATE
    private List<PredictionsViewState> map(Predictions predictions) {

        List<PredictionsViewState> predictionsList = new ArrayList<>();

        for (Prediction prediction: predictions.getPredictions()) {
            predictionsList.add(new PredictionsViewState(
                    prediction.getDescription(),
                    prediction.getPlaceId(),
                    prediction.getStructuredFormatting().getName()
            ));
        }

        return predictionsList;

    }

    public void usersChoice(String predictionText) {
        usersSearchRepository.usersSearch(predictionText);

    }

    public SingleLiveEvent<PermissionsViewAction> getActionSingleLiveEvent() {
        return actionSingleLiveEvent;

    }

    public LiveData<List<PredictionsViewState>> getPredictionsLiveData(){
        return predictionsMediatorLiveData;

    }
}
