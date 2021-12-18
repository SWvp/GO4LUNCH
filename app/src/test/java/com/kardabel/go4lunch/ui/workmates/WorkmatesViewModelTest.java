package com.kardabel.go4lunch.ui.workmates;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModelTest {

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final WorkmatesRepository workmatesRepository =
            Mockito.mock(WorkmatesRepository.class);
    private final UsersWhoMadeRestaurantChoiceRepository mUsersWhoMadeRestaurantChoiceRepository =
            Mockito.mock(UsersWhoMadeRestaurantChoiceRepository.class);

    private final Application application = Mockito.mock(Application.class);

    private final MutableLiveData<List<UserModel>> workmatesRepositoryMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<UserWhoMadeRestaurantChoice>> usersWhoMadeRestaurantChoiceRepositoryMutableLiveData =
            new MutableLiveData<>();

    private WorkMatesViewModel workMatesViewModel;

    @Before
    public void setUp() {

        // STRING RETURNS
        Mockito.doReturn("hasn't decided yet").when(application).getString(R.string.not_decided);
        Mockito.doReturn("(").when(application).getString(R.string.left_bracket);
        Mockito.doReturn(")").when(application).getString(R.string.right_bracket);

        // RETURNS OF MOCKED CLASS
        Mockito.doReturn(workmatesRepositoryMutableLiveData)
                .when(workmatesRepository)
                .getWorkmates();
        Mockito.doReturn(usersWhoMadeRestaurantChoiceRepositoryMutableLiveData)
                .when(mUsersWhoMadeRestaurantChoiceRepository)
                .getWorkmatesWhoMadeRestaurantChoice();

        // SET LIVEDATA VALUES
        workmatesRepositoryMutableLiveData.setValue(workmates());
        usersWhoMadeRestaurantChoiceRepositoryMutableLiveData.setValue(workmatesWhoMadeChoice());

        // SET THE VIEW MODEL WITH THE MOCKED CLASS
        workMatesViewModel = new WorkMatesViewModel(
                application,
                workmatesRepository,
                mUsersWhoMadeRestaurantChoiceRepository
        );

    }

    @Test
    public void nominalCase() {
         // WHEN
        LiveDataTestUtils.observeForTesting(workMatesViewModel.getWorkmatesViewStateLiveData(), workmatesViewState -> {
            // THEN
            assertEquals(WorkmatesViewModelTest.this.getDefaultWorkmatesViewState(), workmatesViewState);

            verify(workmatesRepository).getWorkmates();
            verify(mUsersWhoMadeRestaurantChoiceRepository).getWorkmatesWhoMadeRestaurantChoice();
        });
    }

    @Test
    public void no_workmate_have_chose_restaurant_yet_should_display_only_has_not_decided_status() {
        // GIVEN
        usersWhoMadeRestaurantChoiceRepositoryMutableLiveData.setValue(new ArrayList<>());
        // WHEN
        LiveDataTestUtils.observeForTesting(workMatesViewModel.getWorkmatesViewStateLiveData(), workmatesViewState -> {
            // THEN
            assertEquals(WorkmatesViewModelTest.this.getWorkmatesHaveNotChosenYet(), workmatesViewState);

            verify(workmatesRepository).getWorkmates();
            verify(mUsersWhoMadeRestaurantChoiceRepository).getWorkmatesWhoMadeRestaurantChoice();
        });
    }


    // region IN

    // VAL FOR TESTING
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

    String firstRestaurantId = "First_Restaurant_Id";
    String firstRestaurantName = "First_Restaurant_Name";
    String firstAddress = "First_Restaurant_Address";

    String secondRestaurantId = "Second_Restaurant_Id";
    String secondRestaurantName = "Second_Restaurant_Name";
    String secondAddress = "Second_Restaurant_Address";

    private List<UserModel> workmates() {
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

    private List<UserWhoMadeRestaurantChoice> workmatesWhoMadeChoice() {
        List<UserWhoMadeRestaurantChoice> userWhoMadeRestaurantChoices = new ArrayList<>();
        userWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        firstRestaurantId,
                        firstRestaurantName,
                        thirdUserId,
                        thirdUserName,
                        firstAddress

                )
        );
        userWhoMadeRestaurantChoices.add(
                new UserWhoMadeRestaurantChoice(
                        secondRestaurantId,
                        secondRestaurantName,
                        fourthUserId,
                        fourthUserName,
                        secondAddress

                )
        );
        return userWhoMadeRestaurantChoices;
    }

    // endregion

    /////////////////////////////////////////////////////////

    // region OUT

    // VAL FOR TESTING
    String hasNotDecidedYet = "hasn't decided yet";
    int gray = -7829368;
    int black = -16777216;

    private List<WorkMateViewState> getDefaultWorkmatesViewState() {
        List<WorkMateViewState> workMateViewStates = new ArrayList<>();
        workMateViewStates.add(new WorkMateViewState(
                thirdUserName,
                thirdUserName + " "  + "(" + firstRestaurantName + ")",
                thirdAvatar,
                thirdUserId,
                true,
                black
        ));
        workMateViewStates.add(new WorkMateViewState(
                fourthUserName,
                fourthUserName + " "  + "(" + secondRestaurantName + ")",
                fourthAvatar,
                fourthUserId,
                true,
                black
        ));
        workMateViewStates.add(new WorkMateViewState(
                firstUserName,
                firstUserName + " "  + hasNotDecidedYet,
                firstAvatar,
                firstUserId,
                false,
                gray
        ));
        workMateViewStates.add(new WorkMateViewState(
                secondUserName,
                secondUserName + " "  + hasNotDecidedYet,
                secondAvatar,
                secondUserId,
                false,
                gray
        ));
        return workMateViewStates;
    }

    private List<WorkMateViewState> getWorkmatesHaveNotChosenYet() {
        List<WorkMateViewState> workMateViewStates = new ArrayList<>();
        workMateViewStates.add(new WorkMateViewState(
                firstUserName,
                firstUserName + " "  + hasNotDecidedYet,
                firstAvatar,
                firstUserId,
                false,
                gray
        ));
        workMateViewStates.add(new WorkMateViewState(
                secondUserName,
                secondUserName + " "  + hasNotDecidedYet,
                secondAvatar,
                secondUserId,
                false,
                gray
        ));
        workMateViewStates.add(new WorkMateViewState(
                thirdUserName,
                thirdUserName + " "  + hasNotDecidedYet,
                thirdAvatar,
                thirdUserId,
                false,
                gray
        ));
        workMateViewStates.add(new WorkMateViewState(
                fourthUserName,
                fourthUserName + " "  + hasNotDecidedYet,
                fourthAvatar,
                fourthUserId,
                false,
                gray
        ));
        return workMateViewStates;
    }
}
