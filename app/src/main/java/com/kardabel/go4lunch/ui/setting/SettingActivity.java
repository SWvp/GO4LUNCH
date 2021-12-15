package com.kardabel.go4lunch.ui.setting;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kardabel.go4lunch.databinding.SettingsActivityBinding;
import com.kardabel.go4lunch.workmanager.UploadWorker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class SettingActivity extends AppCompatActivity {

    public static final String REMINDER_REQUEST = "my reminder";
    public static final String SHARED_PREFS = "shared reminder";
    private SettingsActivityBinding binding;
    public final WorkManager workManager = WorkManager.getInstance(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.switchNotification.setOnClickListener(view -> {
            viewModel.notificationChange();
        });

        binding.settingsToolbar.setOnClickListener(view -> onBackPressed());
    }

    // TODO Stephane ViewModel !
    private void activateNotification() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime thisNoon = currentDate.with(LocalTime.of(12, 0));

        if (currentDate.isAfter(thisNoon)) {
            thisNoon = thisNoon.plusDays(1);
        }

        long timeLeft = ChronoUnit.MILLIS.between(currentDate, thisNoon);

//        timeLeft = thisNoon
//            .atZone(ZoneId.systemDefault())
//            .toInstant()
//            .toEpochMilli()
//            - currentDate
//            .atZone(ZoneId.systemDefault()).toInstant()
//            .toEpochMilli();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UploadWorker.class, 1, TimeUnit.DAYS)
            // .setInputData(userId)
            .setInitialDelay(timeLeft, TimeUnit.MILLISECONDS)
            .build();

        workManager.enqueueUniquePeriodicWork(REMINDER_REQUEST, ExistingPeriodicWorkPolicy.REPLACE, workRequest);
        Toast.makeText(this, "notification enable", Toast.LENGTH_LONG).show();

    }

    private void deactivateNotification() {
        workManager.cancelAllWork();
        Toast.makeText(this, "notification disabled", Toast.LENGTH_LONG).show();

    }
}
