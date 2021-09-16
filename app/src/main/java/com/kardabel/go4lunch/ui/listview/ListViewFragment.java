package com.kardabel.go4lunch.ui.listview;

import android.content.Context;
import android.content.Intent;
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

import com.kardabel.go4lunch.databinding.FragmentListviewBinding;
import com.kardabel.go4lunch.di.ListViewViewModelFactory;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;

public class ListViewFragment extends Fragment {

    private ListViewViewModel listViewViewModel;
    private FragmentListviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListviewBinding.inflate(inflater, container, false);
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
        ListViewViewModelFactory listViewModelFactory = ListViewViewModelFactory.getInstance();
        listViewViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(ListViewViewModel.class);

        // CONFIGURE RECYCLERVIEW
        listViewViewModel.getListViewViewStateLiveData().observe(getViewLifecycleOwner(), new Observer<RestaurantsWrapperViewState>() {
            @Override
            public void onChanged(RestaurantsWrapperViewState restaurantsWrapperViewState) {
                adapter.setRestaurantListData(restaurantsWrapperViewState.getItemRestaurant());

            }
        });

        // ON ITEM CLICK, GO TO DETAILS
        adapter.setOnItemClickListener(new RestaurantItemRecyclerViewAdapter.OnRestaurantItemClickListener() {
            @Override
            public void onRestaurantItemClick(RestaurantItemViewState restaurantItemViewState) {
                Intent intent = new Intent(requireActivity(), RestaurantDetailsActivity.class);
                intent.putExtra(RestaurantDetailsActivity.RESTAURANT_ID, restaurantItemViewState.getPlaceId());
                startActivity(intent);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}