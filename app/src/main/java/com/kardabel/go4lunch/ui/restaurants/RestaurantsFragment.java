package com.kardabel.go4lunch.ui.restaurants;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kardabel.go4lunch.databinding.RecyclerviewRestaurantsBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;

public class RestaurantsFragment extends Fragment {

    private RecyclerviewRestaurantsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = RecyclerviewRestaurantsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();

        RestaurantRecyclerViewAdapter adapter = new RestaurantRecyclerViewAdapter();

        binding.restaurantListRecyclerView.setAdapter(adapter);
        binding.restaurantListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.restaurantListRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));

        // INJECTION OF RESTAURANT VIEWMODEL
        ViewModelFactory restaurantsViewModelFactory = ViewModelFactory.getInstance();
        RestaurantsViewModel restaurantsViewModel = new ViewModelProvider(this, restaurantsViewModelFactory).get(RestaurantsViewModel.class);

        // CONFIGURE RECYCLERVIEW
        restaurantsViewModel.getRestaurantsViewStateLiveData().observe(getViewLifecycleOwner(), restaurantsWrapperViewState -> adapter.setRestaurantListData(restaurantsWrapperViewState.getItemRestaurant()));

        // ON ITEM CLICK, GO TO DETAILS
        adapter.setOnItemClickListener(restaurantsViewState -> startActivity(RestaurantDetailsActivity.navigate(requireContext(), restaurantsViewState.getPlaceId())));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}