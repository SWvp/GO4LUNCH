package com.kardabel.go4lunch.workmanager;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.kardabel.go4lunch.MainActivity;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UploadWorker extends Worker {


    public static final String USERS = "users";
    private static final String RESTAURANT_ID = "restaurantId";
    private static final int RC_PENDING_INTENT = 44;

    private List<UserWhoMadeRestaurantChoice> usersWithRestaurant = new ArrayList<>();
    private String restaurantName = null;
    private String restaurantAddress = null;
    private String restaurantId = null;

    private Context context;

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {

        super(context, workerParams);

        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {

        getUsersWithRestaurantChoice();

        if (isCurrentUserHasChosenRestaurant()) {

            List<String> workmates = new ArrayList<>();

            for (UserWhoMadeRestaurantChoice userWhoMadeRestaurantChoice : usersWithRestaurant) {

                if (userWhoMadeRestaurantChoice.getRestaurantName().equals(restaurantName)) {
                    workmates.add(userWhoMadeRestaurantChoice.getUserId());

                }
            }

            List<String> allWorkmate = new ArrayList<>();

            for (String workmateId : workmates) {
                getUserDocument(workmateId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        allWorkmate.add(user.getUserName());

                    }
                });
            }

            notificationMessage(allWorkmate);
        }

        return Result.success();

    }

    private void getUsersWithRestaurantChoice() {

        usersWithRestaurant.clear();
        getDayCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("restaurant choice error", error.getMessage());
                    return;
                }
                //  List<WorkmateWhoMadeRestaurantChoice> userWithRestaurant = new ArrayList<>();

                assert value != null;
                for (DocumentChange document : value.getDocumentChanges()) {
                    Log.d("pipo", "onEvent() called with: value = [" + document.getDocument().toObject(UserWhoMadeRestaurantChoice.class) + "], error = [" + error + "]");
                    if (document.getType() == DocumentChange.Type.ADDED) {

                        usersWithRestaurant.add(document.getDocument().toObject(UserWhoMadeRestaurantChoice.class));

                    } else if (document.getType() == DocumentChange.Type.MODIFIED) {

                        for (int i = 0; i < usersWithRestaurant.size(); i++) {
                            if (usersWithRestaurant.get(i).getUserId().equals(document.getDocument().toObject(UserWhoMadeRestaurantChoice.class).getUserId())) {
                                usersWithRestaurant.remove(usersWithRestaurant.get(i));
                            }

                        }

                        usersWithRestaurant.add(document.getDocument().toObject(UserWhoMadeRestaurantChoice.class));

                    } else if (document.getType() == DocumentChange.Type.REMOVED) {


                        usersWithRestaurant.remove(document.getDocument().toObject(UserWhoMadeRestaurantChoice.class));

                    }
                }
            }
        });
    }

    private boolean isCurrentUserHasChosenRestaurant() {
        boolean gotRestaurant = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for (UserWhoMadeRestaurantChoice userWhoMadeRestaurantChoice : usersWithRestaurant) {

            assert user != null;
            if (userWhoMadeRestaurantChoice.getUserId().equals(user.getUid())) {
                restaurantName = userWhoMadeRestaurantChoice.getRestaurantName();
                restaurantAddress = userWhoMadeRestaurantChoice.getRestaurantAddress();
                gotRestaurant = true;
            }
        }
        return gotRestaurant;
    }

    private void notificationMessage(List<String> allWorkmate) {
        String notification;
        StringBuilder stringBuilderWorkmates = new StringBuilder();
        if (!allWorkmate.isEmpty()) {

            for (int i = 0; i < allWorkmate.size(); i++) {
                stringBuilderWorkmates.append(allWorkmate.get(i));
                if (i < allWorkmate.size() - 2)
                    stringBuilderWorkmates.append(", ");
                else if (i == allWorkmate.size() - 2)
                    stringBuilderWorkmates.append("&");
            }

            notification = restaurantName
                    + " "
                    + restaurantAddress
                    + " " + stringBuilderWorkmates;

        }else {
            notification = restaurantName
                    + " "
                    + restaurantAddress;
        }

        displayNotification(notification);

    }

    private void displayNotification(String notification) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(RESTAURANT_ID, restaurantId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, RC_PENDING_INTENT, intent, PendingIntent.FLAG_UPDATE_CURRENT);


    }


    public CollectionReference getDayCollection() {
        return FirebaseFirestore.getInstance().collection(LocalDate.now().toString());
    }

    public CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(USERS);
    }

    public DocumentReference getUserDocument(String workmateId) {
        return getUserCollection().document(workmateId);
    }


}
