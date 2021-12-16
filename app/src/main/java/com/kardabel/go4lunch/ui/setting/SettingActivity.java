package com.kardabel.go4lunch.ui.setting;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.databinding.SettingsActivityBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;

public class SettingActivity extends AppCompatActivity {

    private SettingsActivityBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // INIT SETTING VIEW MODEL
        ViewModelFactory settingViewModelFactory = ViewModelFactory.getInstance();
        SettingViewModel settingViewModel =
                new ViewModelProvider(this, settingViewModelFactory)
                        .get(SettingViewModel.class);

        // OBSERVER
        settingViewModel.getSwitchPosition().observe(this, switchPosition -> {
            switch (switchPosition) {
                case 1:
                    binding.switchNotification.setChecked(false);
                    break;
                case 2:
                    binding.switchNotification.setChecked(true);
                    break;
            }
        });

        // SINGLE LIVE EVENT TO DISPLAY WHICH NOTIFICATION STATUS IS ON
        settingViewModel.getActionSingleLiveEvent().observe(this, permissionsViewAction -> {
            switch (permissionsViewAction) {
                case NOTIFICATION_DISABLED:
                    Toast.makeText(SettingActivity.this, getString(
                            R.string.settings_notification_disabled),
                            Toast.LENGTH_SHORT)
                            .show();
                    break;
                case NOTIFICATION_ENABLED:
                    Toast.makeText(SettingActivity.this, getString(
                            R.string.settings_notification_activated),
                            Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        });

        binding.switchNotification.setOnClickListener(view -> settingViewModel.notificationChange());

        binding.settingsToolbar.setOnClickListener(view -> onBackPressed());
    }
}
