package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.Prediction;
import com.kardabel.go4lunch.pojo.Predictions;
import com.kardabel.go4lunch.repository.AutocompleteRepository;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsViewState;
import com.kardabel.go4lunch.usecase.GetPredictionsUseCase;
import com.kardabel.go4lunch.util.PermissionsViewAction;
import com.kardabel.go4lunch.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MediatorLiveData<List<Prediction>> predictionsMediatorLiveData = new MediatorLiveData<>();

    private SingleLiveEvent<PermissionsViewAction> actionSingleLiveEvent = new SingleLiveEvent<>();

    private Application application;

    private LocationRepository locationRepository;
    private WorkmatesRepository workmatesRepository;
    private GetPredictionsUseCase getPredictionsUseCase;


    public MainActivityViewModel(
            @NonNull Application application,
            @NonNull LocationRepository locationRepository,
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull GetPredictionsUseCase getPredictionsUseCase) {
        super();
        this.application = application;
        this.locationRepository = locationRepository;
        this.workmatesRepository = workmatesRepository;
        this.getPredictionsUseCase = getPredictionsUseCase;

    }

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
        workmatesRepository.getRestaurantsAddedAsFavorite();

    }

    public void submitSearch(String text){
        List<Prediction> predictionsList = new ArrayList<>();


        LiveData<Predictions> predictionsLiveData = getPredictionsUseCase.invoke(text);



        predictionsMediatorLiveData.addSource(predictionsLiveData, new Observer<Predictions>() {
            @Override
            public void onChanged(Predictions predictions) {
                predictionsList.addAll(predictions.getPredictions());
                predictionsMediatorLiveData.setValue(predictionsList);
            }
        });

    }

    public SingleLiveEvent<PermissionsViewAction> getActionSingleLiveEvent() {
        return actionSingleLiveEvent;

    }

    public LiveData<List<Prediction>> getPredictionsLiveData(){
        return predictionsMediatorLiveData;

    }
}
