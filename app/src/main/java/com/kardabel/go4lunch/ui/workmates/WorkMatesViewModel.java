package com.kardabel.go4lunch.ui.workmates;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.WorkmateWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.WorkmatesWhoMadeRestaurantChoiceRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkMatesViewModel extends ViewModel {

    private final MediatorLiveData<List<WorkMatesViewState>> workMatesViewStateMediatorLiveData = new MediatorLiveData<>();

    private final Application application;

    public WorkMatesViewModel(
            @NonNull Application application,
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull WorkmatesWhoMadeRestaurantChoiceRepository workmatesWhoMadeRestaurantChoiceRepository
    ) {

        this.application = application;

        // HERE WE HAVE 2 COLLECTIONS TO OBSERVE:
        // ONE WITH ALL REGISTERED USERS
        // AND ONE WITH USERS (WORKMATES) WHO MADE A CHOICE
        LiveData<List<UserModel>> workMatesLiveData =
                workmatesRepository.getWorkmates();
        LiveData<List<WorkmateWhoMadeRestaurantChoice>> workmatesWhoMadeChoiceLiveData =
                workmatesWhoMadeRestaurantChoiceRepository.getWorkmatesWhoMadeRestaurantChoice();

        // OBSERVERS
        workMatesViewStateMediatorLiveData.addSource(workMatesLiveData, workmates ->
                combine(
                        workmates,
                        workmatesWhoMadeChoiceLiveData.getValue()));
        workMatesViewStateMediatorLiveData.addSource(workmatesWhoMadeChoiceLiveData, workmateWhoMadeRestaurantChoices ->
                combine(
                        workMatesLiveData.getValue(),
                        workmateWhoMadeRestaurantChoices));

    }

    // COMBINE THE 2 SOURCES
    private void combine(
            List<UserModel> workmates,
            List<WorkmateWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {

        if (workmatesWhoMadeRestaurantChoices != null) {
            workMatesViewStateMediatorLiveData.setValue(mapWorkmates(
                    workmates,
                    workmatesWhoMadeRestaurantChoices));

        }
    }

    // MAP TO WORKMATE VIEW STATE
    private List<WorkMatesViewState> mapWorkmates(List<UserModel> workmates,
                                                  List<WorkmateWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {
        List<WorkMatesViewState> workMatesViewStateList = new ArrayList<>();

        for (int i = 0; i < workmates.size(); i++) {


            String workmateName = workmates.get(i).getUserName();
            String avatar = workmates.get(i).getAvatarURL();
            String workmateId = workmates.get(i).getUid();
            String restaurant = userChoice(workmateId, workmatesWhoMadeRestaurantChoices);

            int colorText = Color.GRAY;
            if(!userChoice(workmateId, workmatesWhoMadeRestaurantChoices).equals(application.getString(R.string.not_decided))){
                colorText = Color.BLACK;


            }

            workMatesViewStateList.add(new WorkMatesViewState(
                    workmateName,
                    workmateName + restaurant,
                    avatar,
                    workmateId,
                    isUserHasDecided(workmateId, workmatesWhoMadeRestaurantChoices),
                    colorText

            ));
        }
        return workMatesViewStateList;

    }

    // GET THE LUNCH CHOICE STATUS OF EVERY WORKMATES
    private boolean isUserHasDecided(String workmateId,
                                     List<WorkmateWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {
        boolean state = false;

        for (int i = 0; i < workmatesWhoMadeRestaurantChoices.size(); i++) {
            if (workmatesWhoMadeRestaurantChoices.get(i).getUserId().equals(workmateId)) {
                state = true;

            }
        }
        return state;

    }

    // GET THE RESTAURANT WHO HAS BEEN CHOSEN
    private String userChoice(@NonNull String workmateId,
                              @NonNull List<WorkmateWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {
        String restaurantName = application.getString(R.string.not_decided);

        for (int i = 0; i < workmatesWhoMadeRestaurantChoices.size(); i++) {
            if (workmatesWhoMadeRestaurantChoices.get(i).getUserId().equals(workmateId)) {
                restaurantName = application.getString(R.string.left_bracket) +
                        workmatesWhoMadeRestaurantChoices.get(i).getRestaurantName() +
                        application.getString(R.string.right_bracket);

            }
        }
        return restaurantName;

    }

    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<List<WorkMatesViewState>> getWorkmatesViewStateLiveData() {
        return workMatesViewStateMediatorLiveData;

    }
}