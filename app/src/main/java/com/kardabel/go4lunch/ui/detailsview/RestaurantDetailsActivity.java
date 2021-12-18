package com.kardabel.go4lunch.ui.detailsview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.databinding.RestaurantDetailsBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;

public class RestaurantDetailsActivity extends AppCompatActivity {

    public static final String RESTAURANT_ID = "RESTAURANT_ID";
    private String restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPhoneNumber;
    private String restaurantWebsite;

    private RestaurantDetailsBinding binding;
    private RestaurantDetailsViewModel restaurantDetailsViewModel;

    public static Intent navigate(Context context, String placeId) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(RESTAURANT_ID, placeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RestaurantDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RestaurantDetailsRecyclerViewAdapter adapter = new RestaurantDetailsRecyclerViewAdapter();

        binding.detailRecyclerView.setAdapter(adapter);
        binding.detailRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        RestaurantDetailsActivity.this,
                        RecyclerView.VERTICAL,
                        false));

        // CONFIGURE VIEWMODEL
        ViewModelFactory listViewModelFactory = ViewModelFactory.getInstance();
        restaurantDetailsViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(RestaurantDetailsViewModel.class);

        // INIT THE VIEW MODEL WITH THE RESTAURANT ID PASSED IN INTENT
        Intent intent = getIntent();
        restaurantDetailsViewModel.init(intent.getStringExtra(RESTAURANT_ID));

        // FEED THE DETAILS VIEW
        restaurantDetailsViewModel.getRestaurantDetailsViewStateLiveData()
                .observe(this, details -> {

                    restaurantId = details.getDetailsRestaurantId();
                    restaurantName = details.getDetailsRestaurantName();
                    restaurantAddress = details.getDetailsRestaurantAddress();
                    restaurantPhoneNumber = details.getDetailsRestaurantNumber();
                    restaurantWebsite = details.getDetailsWebsite();

                    binding.detailRestaurantName.setText(details.getDetailsRestaurantName());
                    binding.detailRestaurantAddress.setText(details.getDetailsRestaurantAddress());
                    Glide.with(binding.detailPicture.getContext())
                            .load(details.getDetailsPhoto())
                            .into(binding.detailPicture);
                    binding.detailsRating.setRating((float) details.getRating());
                    binding.choseRestaurantButton.setImageResource(details.getChoseRestaurantButton());
                    binding.choseRestaurantButton.setColorFilter(details.getBackgroundColor());
                    binding.detailLikeButton.setImageResource(details.getDetailLikeButton());

                });

        // FEED THE ADAPTER IF NEEDED
        restaurantDetailsViewModel.getWorkmatesWhoChoseThisRestaurant()
                .observe(this, adapter::setWorkmatesListData);

        binding.choseRestaurantButton.setOnClickListener(v -> restaurantDetailsViewModel.onChoseRestaurantButtonClick(
                restaurantId,
                restaurantName,
                restaurantAddress));

        binding.detailLikeButton.setOnClickListener(v -> restaurantDetailsViewModel.onFavoriteIconClick(
                restaurantId,
                restaurantName));

        // CALL THE RESTAURANT
        binding.callIcon.setOnClickListener(v -> {
            if (getString(R.string.no_phone_number).equals(restaurantPhoneNumber)) {
                Toast.makeText(
                        RestaurantDetailsActivity.this,
                        getString(R.string.no_phone_number_message),
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent intent1 =
                        new Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse(getString(R.string.tel) + restaurantPhoneNumber));
                startActivity(intent1);
            }
        });

        // GO TO THE RESTAURANT WEBSITE
        binding.webIcon.setOnClickListener(v -> {
            switch (restaurantWebsite) {
                case "https://www.google.com/":
                    Toast.makeText(
                            RestaurantDetailsActivity.this,
                            getString(R.string.no_website),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    Intent intent12 = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantWebsite));
                    startActivity(intent12);

            }
        });

    }
}
