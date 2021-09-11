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

import com.kardabel.go4lunch.UserActivityViewModel;
import com.kardabel.go4lunch.databinding.FragmentListviewBinding;
import com.kardabel.go4lunch.di.ListViewViewModelFactory;

import java.util.List;

public class ListViewFragment extends Fragment {

    private ListViewViewModel mListViewViewModel;
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
        ListViewRecyclerViewAdapter adapter = new ListViewRecyclerViewAdapter();
        binding.restaurantListRecyclerView.setAdapter(adapter);
        binding.restaurantListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.restaurantListRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));



        //mListViewViewModel = new ViewModelProvider(this, ListViewViewModelFactory.getInstance()).get(com.kardabel.go4lunch.ui.listview.ListViewViewModel.class);

      //  mListViewViewModel =
       //         new ViewModelProvider(this).get(ListViewViewModel.class);

        ListViewViewModelFactory listViewModelFactory = ListViewViewModelFactory.getInstance();

        mListViewViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(ListViewViewModel.class);


        mListViewViewModel.getListViewViewStateLiveData().observe(getViewLifecycleOwner(), new Observer<ListViewViewState>() {
            @Override
            public void onChanged(ListViewViewState listViewViewState) {
                adapter.setRestaurantListData(listViewViewState.getItemRestaurant());

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}