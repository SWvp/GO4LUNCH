package com.kardabel.go4lunch.ui.setting;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.databinding.SettingsActivityBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivityBinding binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // INIT SETTING VIEW MODEL
        ViewModelFactory settingViewModelFactory = ViewModelFactory.getInstance();
        SettingViewModel settingViewModel =
            new ViewModelProvider(this, settingViewModelFactory)
                .get(SettingViewModel.class);

        // OBSERVER
        settingViewModel.isNotificationEnabledLiveData().observe(this, isNotificationEnabled ->
            binding.switchNotification.setChecked(isNotificationEnabled)
        );

        settingViewModel.getToastMessageSingleLiveEvent().observe(this, message ->
            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show()
        );

        binding.switchNotification.setOnClickListener(view -> {
            settingViewModel.onSwitchNotificationClicked();
        });

        binding.settingsToolbar.setOnClickListener(view -> onBackPressed());
    }
}
