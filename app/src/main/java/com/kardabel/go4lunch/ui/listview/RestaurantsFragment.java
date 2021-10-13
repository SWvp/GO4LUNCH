package com.kardabel.go4lunch.ui.listview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kardabel.go4lunch.databinding.RecyclerviewListviewBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;

public class RestaurantsFragment extends Fragment {

    private RestaurantsViewModel mRestaurantsViewModel;
    private RecyclerviewListviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = RecyclerviewListviewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();

        RestaurantItemRecyclerViewAdapter adapter = new RestaurantItemRecyclerViewAdapter();

        binding.restaurantListRecyclerView.setAdapter(adapter);
        binding.restaurantListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.restaurantListRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));

        // INJECTION OF LISTVIEWMODEL
        ViewModelFactory listViewModelFactory = ViewModelFactory.getInstance();
        mRestaurantsViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(RestaurantsViewModel.class);

        // CONFIGURE RECYCLERVIEW
        mRestaurantsViewModel.getRestaurantsViewStateLiveData().observe(getViewLifecycleOwner(), new Observer<RestaurantsWrapperViewState>() {
            @Override
            public void onChanged(RestaurantsWrapperViewState restaurantsWrapperViewState) {
                adapter.setRestaurantListData(restaurantsWrapperViewState.getItemRestaurant());

            }
        });

        // ON ITEM CLICK, GO TO DETAILS
        adapter.setOnItemClickListener(new RestaurantItemRecyclerViewAdapter.OnRestaurantItemClickListener() {
            @Override
            public void onRestaurantItemClick(RestaurantsViewState restaurantsViewState) {

                startActivity(RestaurantDetailsActivity.navigate(requireContext(), restaurantsViewState.getPlaceId()));

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}