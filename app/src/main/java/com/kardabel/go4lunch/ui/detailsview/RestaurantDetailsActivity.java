package com.kardabel.go4lunch.ui.detailsview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.databinding.RestaurantDetailsBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;

import java.util.List;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private static final String RESTAURANT_ID = "RESTAURANT_ID";
    private String restaurantId;
    private String restaurantName;
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

        DetailsRecyclerViewAdapter adapter = new DetailsRecyclerViewAdapter();

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
        restaurantDetailsViewModel.getRestaurantDetailsViewStateLiveData().observe(this, new Observer<RestaurantDetailsViewState>() {
            @Override
            public void onChanged(RestaurantDetailsViewState details) {

                restaurantName = details.getDetailsRestaurantName();
                restaurantId = details.getDetailsRestaurantId();

                binding.detailRestaurantName.setText(details.getDetailsRestaurantName());
                binding.detailRestaurantAddress.setText(details.getDetailsRestaurantAddress());
                Glide.with(binding.detailPicture.getContext())
                        .load(details.getDetailsPhoto())
                        .into(binding.detailPicture);
                //number
                //website
                binding.detailsRating.setRating((float) details.getRating());
                binding.choseRestaurantButton.setImageResource(details.getChoseRestaurantButton());
                binding.detailLikeButton.setImageResource(details.getDetailLikeButton());

                restaurantPhoneNumber = details.getDetailsRestaurantNumber();
                restaurantWebsite = details.getDetailsWebsite();
            }
        });

        // FEED THE ADAPTER IF NEEDED
        restaurantDetailsViewModel.getWorkmatesLikeThisRestaurant().observe(this, new Observer<List<DetailsWorkmatesViewState>>() {
            @Override
            public void onChanged(List<DetailsWorkmatesViewState> workMates) {
                adapter.setWorkmatesListData(workMates);
            }
        });

        binding.choseRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantDetailsViewModel.onChoseRestaurantButtonClick(restaurantId, restaurantName);
            }
        });

        binding.detailLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantDetailsViewModel.onFavoriteIconClick(restaurantId, restaurantName);
            }
        });

        // CALL THE RESTAURANT
        binding.callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (restaurantPhoneNumber) {
                    case "0":
                        Toast.makeText(RestaurantDetailsActivity.this, "This restaurant doesn't have phone number", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurantPhoneNumber));
                        startActivity(intent);
                }
            }
        });

        // GO TO THE RESTAURANT WEBSITE
        binding.webIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (restaurantWebsite) {
                    case "https://www.google.com/":
                        Toast.makeText(RestaurantDetailsActivity.this, "It seems your restaurant isn't on the web, but you can try on google !", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantWebsite));
                        startActivity(intent);
                }
            }
        });

    }
}
