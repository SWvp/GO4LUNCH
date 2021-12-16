package com.kardabel.go4lunch.ui.setting;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
        settingViewModel.getSwitchPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer switchPosition) {
                switch (switchPosition) {
                    case 1:
                        binding.switchNotification.setChecked(false);

                        break;
                    case 2:
                        binding.switchNotification.setChecked(true);


                        break;
                }
            }
        });

        binding.switchNotification.setOnClickListener(view -> {
            settingViewModel.notificationChange();
            if (binding.switchNotification.isChecked()) {

                Toast
                        .makeText(SettingActivity.this, "notification enabled", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast
                        .makeText(SettingActivity.this, "notification disabled", Toast.LENGTH_SHORT)
                        .show();

            }
        });

        binding.settingsToolbar.setOnClickListener(view -> onBackPressed());
    }
}
