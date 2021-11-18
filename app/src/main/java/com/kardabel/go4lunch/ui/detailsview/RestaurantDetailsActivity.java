package com.kardabel.go4lunch.ui.detailsview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.databinding.RecyclerviewWorkmatesBinding;
import com.kardabel.go4lunch.databinding.RestaurantDetailsBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;

import java.util.List;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private static final String RESTAURANT_ID = "RESTAURANT_ID";
    private String restaurantId;
    private String restaurantName;

    private RestaurantDetailsBinding binding;
    private RestaurantDetailsViewModel restaurantDetailsViewModel;

    public static Intent navigate(Context context, String placeId){
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
        binding.detailRecyclerView.setLayoutManager(new LinearLayoutManager(RestaurantDetailsActivity.this, RecyclerView.VERTICAL,false));

        // CONFIGURE VIEWMODEL
        ViewModelFactory listViewModelFactory = ViewModelFactory.getInstance();
        restaurantDetailsViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(RestaurantDetailsViewModel.class);

        Intent intent = getIntent();
        restaurantDetailsViewModel.init(intent.getStringExtra(RESTAURANT_ID));

        // FEED THE DETAILS VIEW
        restaurantDetailsViewModel.getRestaurantDetailsViewStateLiveData().observe(this, new Observer<RestaurantDetailsViewState>() {
            @Override
            public void onChanged(RestaurantDetailsViewState details) {

                restaurantId = details.getDetailsRestaurantId();
                restaurantName = details.getDetailsRestaurantName();
                binding.detailRestaurantName.setText(details.getDetailsRestaurantName());
                binding.detailRestaurantAddress.setText(details.getDetailsRestaurantAddress());
                binding.detailsRating.setRating((float) details.getRating());
                binding.detailLikeButton.setImageResource(details.isFavorite());
                String urlPhoto = RestaurantDetailsViewState.urlPhotoDetails(details);
                Glide.with(binding.detailPicture.getContext())
                        .load(urlPhoto)
                        .into(binding.detailPicture);
            }
        });

        // FEED THE ADAPTER IF NEEDED
        restaurantDetailsViewModel.getWorkmatesLikeThisRestaurant().observe(this, new Observer<List<DetailsWorkmatesViewState>>() {
            @Override
            public void onChanged(List<DetailsWorkmatesViewState> workMates) {
                adapter.setWorkmatesListData(workMates);
            }
        });

        binding.detailLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantDetailsViewModel.onFavoriteClick(restaurantId, restaurantName);
            }
        });
    }
 }
