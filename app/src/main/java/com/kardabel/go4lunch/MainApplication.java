package com.kardabel.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class MainApplication extends Application {

    private static Application sApplication;

    private static final String CHANNEL_ID = "notif";

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication=this;

        createNotificationChannel();
    }

    public static Application getApplication() {
        return sApplication;
    }

    // FOR ANDROID 8.0 AND HIGHER, REGISTER APP'S NOTIFICATION CHANNEL
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
