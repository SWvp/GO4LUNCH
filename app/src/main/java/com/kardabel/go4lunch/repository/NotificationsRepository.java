package com.kardabel.go4lunch.repository;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NotificationsRepository {

    public static final String REMINDER_REQUEST = "my reminder";
    public static final String SHARED_PREFS = "shared reminder";

    // SHARED PREFERENCE STORE VALUE OF THE SWITCH BUTTON FOR CURRENT USER
    private final SharedPreferences sharedPref;

    public NotificationsRepository(Context context) {
        sharedPref = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
    }

    public void switchNotification() {
        sharedPref
            .edit()
            .putBoolean(REMINDER_REQUEST, !isNotificationEnabled())
            .apply();
    }

    public LiveData<Boolean> isNotificationEnabledLiveData() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();

        mutableLiveData.setValue(isNotificationEnabled());

        sharedPref.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals(REMINDER_REQUEST)) {
                mutableLiveData.setValue(sharedPreferences.getBoolean(key, false));
            }
        });

        return mutableLiveData;
    }

    private boolean isNotificationEnabled() {
        return sharedPref.getBoolean(REMINDER_REQUEST, false);
    }
}
