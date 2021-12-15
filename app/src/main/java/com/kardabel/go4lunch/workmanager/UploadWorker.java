package com.kardabel.go4lunch.workmanager;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UploadWorker extends Worker {


    public static final String USERS = "users";
    private static final String RESTAURANT_ID = "restaurantId";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "notif";

    private String restaurantName = null;
    private String restaurantAddress = null;
    private String restaurantId = null;

    private final Context context;

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {

        super(context, workerParams);

        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {

        List<UserWhoMadeRestaurantChoice> usersWithRestaurant = getUsersWithRestaurantChoice();

        if (isCurrentUserHasChosenRestaurant(usersWithRestaurant)) {

            List<String> workmates = new ArrayList<>();

            for (UserWhoMadeRestaurantChoice userWhoMadeRestaurantChoice : usersWithRestaurant) {

                if (userWhoMadeRestaurantChoice.getRestaurantName().equals(restaurantName)) {
                    workmates.add(userWhoMadeRestaurantChoice.getUserId());

                }
            }

            List<String> allWorkmate = new ArrayList<>();

            for (String workmateId : workmates) {
                // TODO Stephane Tasks.await ou ?? mettre le nom dans firestore direct
                getUserDocument(workmateId).get().addOnSuccessListener(documentSnapshot -> {
                    UserModel user = documentSnapshot.toObject(UserModel.class);
                    assert user != null;
                    allWorkmate.add(user.getUserName());

                });
            }

            notificationMessage(allWorkmate);
        }

        return Result.success();

    }

    private List<UserWhoMadeRestaurantChoice> getUsersWithRestaurantChoice() {
        List<UserWhoMadeRestaurantChoice> usersWithRestaurant = new ArrayList<>();

        Tasks.await(getDayCollection().get().addOnCompleteListener((value, error) -> {
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
        }));

        return usersWithRestaurant;
    }

    private boolean isCurrentUserHasChosenRestaurant(List<UserWhoMadeRestaurantChoice> usersWithRestaurant) {
        boolean gotRestaurant = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for (UserWhoMadeRestaurantChoice userWhoMadeRestaurantChoice : usersWithRestaurant) {

            assert user != null;
            if (userWhoMadeRestaurantChoice.getUserId().equals(user.getUid())) {
                restaurantName = userWhoMadeRestaurantChoice.getRestaurantName();
                restaurantAddress = userWhoMadeRestaurantChoice.getRestaurantAddress();
                restaurantId = userWhoMadeRestaurantChoice.getRestaurantId();
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

        } else {
            notification = restaurantName
                    + " "
                    + restaurantAddress;
        }

        displayNotification(notification);

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

    private void displayNotification(String notification) {
        // SET THE VIEW WHERE USER MUST GO WHEN TYPE ON NOTIFICATION
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(RESTAURANT_ID, restaurantId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // CREATE NOTIFICATION
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.lunch_icon)
                .setContentTitle("Time to lunch!")
                // .setContentText(notification)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // SHOW THE NOTIFICATION
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }
}
