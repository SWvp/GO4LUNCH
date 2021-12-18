package com.kardabel.go4lunch.workmanager;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UploadWorker extends androidx.work.Worker {

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

        List<UserWhoMadeRestaurantChoice> usersWithRestaurant = null;
        try {
            usersWithRestaurant = getUsersWithRestaurantChoice();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (usersWithRestaurant != null) {

            if (isCurrentUserHasChosenRestaurant(usersWithRestaurant)) {

                List<String> workmates = new ArrayList<>();

                String userId = context.getString(R.string.user_init_value);
                if (FirebaseAuth
                        .getInstance()
                        .getCurrentUser() != null) {

                    userId = FirebaseAuth
                            .getInstance()
                            .getCurrentUser()
                            .getUid();
                }

                for (UserWhoMadeRestaurantChoice userWhoMadeRestaurantChoice : usersWithRestaurant) {

                    if (userWhoMadeRestaurantChoice
                            .getRestaurantName()
                            .equals(restaurantName) &&
                            !userWhoMadeRestaurantChoice
                                    .getUserId()
                                    .equals(userId)) {
                        workmates.add(userWhoMadeRestaurantChoice.getUserName());

                    }
                }
                notificationMessage(workmates);
            }
        }

        return Result.success();

    }

    private List<UserWhoMadeRestaurantChoice> getUsersWithRestaurantChoice() throws ExecutionException, InterruptedException {
        List<UserWhoMadeRestaurantChoice> usersWithRestaurant = new ArrayList<>();


        Tasks.await(getDayCollection().get().addOnCompleteListener(task -> {

            for (QueryDocumentSnapshot document : task.getResult()) {
                usersWithRestaurant.add(document.toObject(UserWhoMadeRestaurantChoice.class));
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
                    stringBuilderWorkmates.append(context.getString(R.string.separate)).append(" ");
                else if (i == allWorkmate.size() - 2)
                    stringBuilderWorkmates.append(" ").append(context.getString(R.string.and)).append(" ");
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


    private void displayNotification(String notification) {
        // SET THE VIEW WHERE USER MUST GO WHEN TYPE ON NOTIFICATION
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(RestaurantDetailsActivity.RESTAURANT_ID, restaurantId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);

        @SuppressLint("UnspecifiedImmutableFlag")
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // CREATE NOTIFICATION
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.lunch_icon)
                .setContentTitle(context.getString(R.string.notification_title))
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
