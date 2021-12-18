package com.kardabel.go4lunch.ui.workmates;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkMatesViewModel extends ViewModel {

    private final Application application;

    private final MediatorLiveData<List<WorkMateViewState>> workMatesViewStateMediatorLiveData =
            new MediatorLiveData<>();


    public WorkMatesViewModel(
            @NonNull Application application,
            @NonNull WorkmatesRepository workmatesRepository,
            @NonNull UsersWhoMadeRestaurantChoiceRepository usersWhoMadeRestaurantChoiceRepository
    ) {

        this.application = application;

        // HERE WE HAVE 2 COLLECTIONS TO OBSERVE:
        // ONE WITH ALL REGISTERED USERS
        // AND ONE WITH USERS (WORKMATES) WHO MADE A CHOICE
        LiveData<List<UserModel>> workMatesLiveData =
                workmatesRepository.getWorkmates();
        LiveData<List<UserWhoMadeRestaurantChoice>> workmatesWhoMadeChoiceLiveData =
                usersWhoMadeRestaurantChoiceRepository.getWorkmatesWhoMadeRestaurantChoice();

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
            List<UserWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {

        if (workmatesWhoMadeRestaurantChoices != null) {
            workMatesViewStateMediatorLiveData.setValue(mapWorkmates(
                    workmates,
                    workmatesWhoMadeRestaurantChoices));

        }
    }

    // MAP TO WORKMATE VIEW STATE
    private List<WorkMateViewState> mapWorkmates(List<UserModel> workmates,
                                                 List<UserWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {

        List<WorkMateViewState> workMateViewStateList = new ArrayList<>();

        for (int i = 0; i < workmates.size(); i++) {

            String workmateName = workmates.get(i).getUserName();
            String avatar = workmates.get(i).getAvatarURL();
            String workmateId = workmates.get(i).getUid();
            String workmateChoice = workmateChoice(workmateId, workmatesWhoMadeRestaurantChoices);

            int colorText = Color.GRAY;
            if(isUserHasDecided(workmateId, workmatesWhoMadeRestaurantChoices)){
                colorText = Color.BLACK;
            }

            workMateViewStateList.add(new WorkMateViewState(
                    workmateName,
                    workmateName + workmateChoice,
                    avatar,
                    workmateId,
                    isUserHasDecided(workmateId, workmatesWhoMadeRestaurantChoices),
                    colorText

            ));
        }

        // SORT THE LIST BY BOOLEAN, IF TRUE, APPEARS AT THE TOP OF THE LIST
        Collections.sort(workMateViewStateList, (o1, o2) -> Boolean.compare(!o1.isUserHasDecided(), !o2.isUserHasDecided()));
        return workMateViewStateList;

    }

    // GET THE LUNCH CHOICE STATUS OF EVERY WORKMATES
    private boolean isUserHasDecided(String workmateId,
                                     List<UserWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {

        boolean state = false;

        for (int i = 0; i < workmatesWhoMadeRestaurantChoices.size(); i++) {
            if (workmatesWhoMadeRestaurantChoices.get(i).getUserId().equals(workmateId)) {
                state = true;

            }
        }
        return state;

    }

    // GET THE RESTAURANT WHO HAS BEEN CHOSEN
    private String workmateChoice(@NonNull String workmateId,
                                  @NonNull List<UserWhoMadeRestaurantChoice> workmatesWhoMadeRestaurantChoices) {
        String restaurantName = " " + application.getString(R.string.not_decided);

        for (int i = 0; i < workmatesWhoMadeRestaurantChoices.size(); i++) {
            if (workmatesWhoMadeRestaurantChoices.get(i).getUserId().equals(workmateId)) {
                restaurantName = " " + application.getString(R.string.left_bracket) +
                        workmatesWhoMadeRestaurantChoices.get(i).getRestaurantName() +
                        application.getString(R.string.right_bracket);

            }
        }
        return restaurantName;

    }

    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<List<WorkMateViewState>> getWorkmatesViewStateLiveData() {
        return workMatesViewStateMediatorLiveData;

    }
}