package com.kardabel.go4lunch.ui.setting;

import android.content.SharedPreferences;
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

        // SHARED PREFERENCE STORE VALUE OF THE SWITCH BUTTON FOR CURRENT USER
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        binding.switchNotification.setChecked(sharedPref.getBoolean("switch", true));


        binding.switchNotification.setOnClickListener(view -> {
            notificationChange();
            editor.putBoolean("switch", binding.switchNotification.isChecked());
            editor.apply();
        });

        binding.settingsToolbar.setOnClickListener(view -> onBackPressed());

    }

    private void notificationChange() {
        if (binding.switchNotification.isChecked()) {
            activateNotification();
        } else {
            deactivateNotification();

        }
    }

    private void activateNotification() {

        long timeLeft;

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime thisNoon = currentDate.with(LocalTime.of(12, 0));

        if (!currentDate.isBefore(thisNoon)) {
            thisNoon = thisNoon.plusDays(1);

        }
        timeLeft =
        thisNoon.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UploadWorker.class, 1,
                TimeUnit.DAYS)
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
