package com.kardabel.go4lunch.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.databinding.SettingsActivityBinding;
import com.kardabel.go4lunch.workmanager.UploadWorker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class SettingActivity extends AppCompatActivity {


    public static final String PREF_REMINDER = "preference of reminder";
    public static final String REMINDER_REQUEST = "my reminder";
    public static final String SHARED_PREFS = "shared reminder";
    private SettingsActivityBinding binding;
    public final WorkManager workManager = WorkManager.getInstance(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPref = getApplication().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        if(sharedPref.getBoolean(PREF_REMINDER, true)){
            binding.switchNotification.setChecked(true);
        }

        binding.switchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationChange();
            }
        });

        binding.settingsToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void notificationChange() {
        if(binding.switchNotification.isChecked()){
            activateReminder();
        }else {
            deactivateReminder();
        }
    }

    private void activateReminder() {
        long timeLeft;
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime thisNoon = currentDate.with(LocalTime.of(12, 0));
        LocalDateTime tomorrow = currentDate.plus(1, ChronoUnit.DAYS).with(LocalTime.of(12, 0));

        if(currentDate.isBefore(thisNoon)){
             timeLeft =
                    thisNoon.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        }else{
            timeLeft =
                    tomorrow.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        }

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UploadWorker.class, 1,
                TimeUnit.DAYS)
               // .setInputData(userId)
                .setInitialDelay(timeLeft, TimeUnit.MILLISECONDS)
                .build();

        workManager.enqueueUniquePeriodicWork(REMINDER_REQUEST, ExistingPeriodicWorkPolicy.REPLACE, workRequest);
        SharedPreferences sharedPref = getApplication().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPref.edit().putBoolean(PREF_REMINDER, true).apply();




    }

    private void deactivateReminder() {
        workManager.cancelAllWork();
        SharedPreferences sharedPref = getApplication().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPref.edit().putBoolean(PREF_REMINDER, false).apply();

        Toast.makeText(this, "reminder disabled", Toast.LENGTH_LONG).show();
    }

//  public static SharedPreferences getDefaultSharedPreferences (Context context){

//  }


    }
