package com.kardabel.go4lunch.ui.setting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kardabel.go4lunch.repository.NotificationsRepository;
import com.kardabel.go4lunch.workmanager.UploadWorker;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class SettingViewModel extends ViewModel {

    public static final String REMINDER_REQUEST = "my reminder";

    private final MediatorLiveData<Integer> notificationSwitchMediatorLiveData =
            new MediatorLiveData<>();

    private final NotificationsRepository notificationsRepository;
    public final WorkManager workManager;
    public final Clock clock;


    public SettingViewModel(@NonNull NotificationsRepository notificationsRepository,
                            @NonNull Context context,
                            @NonNull Clock clock) {

        this.notificationsRepository = notificationsRepository;
        this.workManager = WorkManager.getInstance(context);
        this.clock = clock;

        LiveData<Boolean> notificationLiveData = notificationsRepository.isNotificationEnabledLiveData();

        // OBSERVERS

        notificationSwitchMediatorLiveData.addSource(notificationLiveData, this::map);
    }

    // IF THE NOTIFICATION IS DISABLED, RETURN 1, ELSE 2
    private void map(Boolean switchPosition) {
        if (switchPosition) {
            notificationSwitchMediatorLiveData.setValue(1);
            workManager.cancelAllWork();
        } else {
            notificationSwitchMediatorLiveData.setValue(2);
            activateNotification();

        }
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

    public void notificationChange() {
        notificationsRepository.switchNotification();

    }

    public LiveData<Integer> getSwitchPosition() {
        return notificationSwitchMediatorLiveData;
    }
}