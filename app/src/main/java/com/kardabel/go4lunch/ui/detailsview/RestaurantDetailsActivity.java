package com.kardabel.go4lunch.ui.detailsview;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.databinding.MainActivityBinding;
import com.kardabel.go4lunch.databinding.RestaurantDetailsBinding;
import com.kardabel.go4lunch.di.ListViewViewModelFactory;
import com.kardabel.go4lunch.ui.listview.RestaurantItemViewState;
import com.kardabel.go4lunch.ui.mapview.MapViewViewModel;

public class RestaurantDetailsActivity extends AppCompatActivity {

    public static final String RESTAURANT_ID = "RESTAURANT_ID";

    private RestaurantDetailsBinding binding;
    private RestaurantDetailsViewModel restaurantDetailsViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RestaurantDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // CONFIGURE MAPVIEWMODEL
        ListViewViewModelFactory listViewModelFactory = ListViewViewModelFactory.getInstance();
        restaurantDetailsViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(RestaurantDetailsViewModel.class);

        Intent intent = getIntent();
        restaurantDetailsViewModel.init(intent.getStringExtra(RESTAURANT_ID));



        restaurantDetailsViewModel.getRestaurantDetailsViewStateLiveData().observe(this, new Observer<RestaurantDetailsViewState>() {
            @Override
            public void onChanged(RestaurantDetailsViewState details) {
                binding.detailRestaurantName.setText(details.getDetailsRestaurantName());
                binding.detailRestaurantAddress.setText(details.getDetailsRestaurantAddress());
                String urlPhoto = RestaurantDetailsViewState.urlPhotoDetails(details);
                Glide.with(binding.detailPicture.getContext())
                        .load(urlPhoto)
                        .into(binding.detailPicture);
            }
        });

    }
}
