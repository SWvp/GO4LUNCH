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

import com.kardabel.go4lunch.pojo.AutocompleteResponse;
import com.kardabel.go4lunch.repository.AutocompleteRepository;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.SearchViewRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.ui.mapview.MapViewState;
import com.kardabel.go4lunch.util.PermissionsViewAction;
import com.kardabel.go4lunch.util.SingleLiveEvent;

public class MainActivityViewModel extends ViewModel {

    private SingleLiveEvent<PermissionsViewAction> actionSingleLiveEvent = new SingleLiveEvent<>();

    private Application application;

    private LocationRepository locationRepository;
    private WorkmatesRepository workmatesRepository;
    private AutocompleteRepository autocompleteRepository;


    public MainActivityViewModel(
            @NonNull Application application,
            @NonNull LocationRepository locationRepository,
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull AutocompleteRepository autocompleteRepository) {
        super();
        this.application = application;
        this.locationRepository = locationRepository;
        this.workmatesRepository = workmatesRepository;
        this.autocompleteRepository = autocompleteRepository;

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
        String description;

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();

            String locationAsText = locationLiveData.getValue().getLatitude() + "," + locationLiveData.getValue().getLongitude();

            autocompleteRepository.autocomplete(locationAsText,text);

            LiveData<AutocompleteResponse> autocompleteResult =  autocompleteRepository.getAutocompleteResultListLiveData(locationAsText,text);
       if(autocompleteResult.getValue().getPredictions() != null){

           for (int i = 0; i < autocompleteResult.getValue().getPredictions().size(); i++) {
               description = autocompleteResult.getValue().getPredictions().get(i).getDescription();

           }

       }





    }

    public SingleLiveEvent<PermissionsViewAction> getActionSingleLiveEvent() {
        return actionSingleLiveEvent;

    }
}
