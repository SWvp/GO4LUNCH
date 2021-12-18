package com.kardabel.go4lunch.ui.setting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.repository.NotificationsRepository;
import com.kardabel.go4lunch.util.SingleLiveEvent;
import com.kardabel.go4lunch.workmanager.UploadWorker;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class SettingViewModel extends ViewModel {

    public static final String REMINDER_REQUEST = "my reminder";

    private final LiveData<Boolean> isNotificationEnabledLiveData;
    private final SingleLiveEvent<String> toastMessageSingleLiveEvent = new SingleLiveEvent<>();

    private final NotificationsRepository notificationsRepository;
    public final WorkManager workManager;
    public final Clock clock;

    private boolean initialized = false;

    public SettingViewModel(@NonNull NotificationsRepository notificationsRepository,
                            @NonNull Context context,
                            @NonNull Clock clock) {

        this.notificationsRepository = notificationsRepository;
        this.workManager = WorkManager.getInstance(context);
        this.clock = clock;

        // OBSERVERS

        isNotificationEnabledLiveData = Transformations.map(notificationsRepository.isNotificationEnabledLiveData(), isNotificationEnabled -> {
                if (isNotificationEnabled) {
                    activateNotification();
                } else {
                    workManager.cancelAllWork();
                }

                if (!initialized) {
                    initialized = true;
                } else if (isNotificationEnabled) {
                    toastMessageSingleLiveEvent.setValue(context.getString(R.string.notification_enabled));
                } else {
                    toastMessageSingleLiveEvent.setValue(context.getString(R.string.notification_disabled));
                }

                return isNotificationEnabled;
            }
        );
    }

    // CHECK NOTIFICATION TIME WITH CURRENT TIME AND SET THE WORKER CLASS
    private void activateNotification() {

        LocalDateTime currentDate = LocalDateTime.now(clock);
        LocalDateTime thisNoon = currentDate.with(LocalTime.of(12, 0));

        if (currentDate.isAfter(thisNoon)) {
            thisNoon = thisNoon.plusDays(1);
        }

        long timeLeft = ChronoUnit
            .MILLIS
            .between(currentDate, thisNoon);

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
            UploadWorker.class,
            1,
            TimeUnit.DAYS)
            .setInitialDelay(timeLeft, TimeUnit.MILLISECONDS)
            .build();

        workManager.enqueueUniquePeriodicWork(
            REMINDER_REQUEST,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest);

    }

    public void onSwitchNotificationClicked() {
        notificationsRepository.switchNotification();
    }

    public LiveData<Boolean> isNotificationEnabledLiveData() {
        return isNotificationEnabledLiveData;
    }

    public SingleLiveEvent<String> getToastMessageSingleLiveEvent() {
        return toastMessageSingleLiveEvent;
    }
}
