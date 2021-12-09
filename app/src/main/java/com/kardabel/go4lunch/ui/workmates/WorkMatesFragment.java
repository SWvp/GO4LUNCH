package com.kardabel.go4lunch.ui.workmates;

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

import com.kardabel.go4lunch.databinding.RecyclerviewWorkmatesBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.ui.chat.ChatActivity;

public class WorkMatesFragment extends Fragment {

    private RecyclerviewWorkmatesBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        binding = RecyclerviewWorkmatesBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();

        // INJECTION OF WORKMATES VIEWMODEL
        ViewModelFactory workmatesViewModelFactory = ViewModelFactory.getInstance();
        WorkMatesViewModel workMatesViewModel =
                new ViewModelProvider(this, workmatesViewModelFactory)
                        .get(WorkMatesViewModel.class);

        // CONFIGURE RECYCLERVIEW
        WorkMatesRecyclerViewAdapter adapter = new WorkMatesRecyclerViewAdapter();
        binding.workmateRecyclerView.setAdapter(adapter);
        binding.workmateRecyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.workmateRecyclerView.setLayoutManager(
                new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        workMatesViewModel.getWorkmatesViewStateLiveData().observe(getViewLifecycleOwner(),
                adapter::setWorkmatesListData);

        adapter.setOnItemClickListener(workMatesViewState -> startActivity(
                ChatActivity.navigate(
                        requireContext(),
                        workMatesViewState.getWorkmateId(),
                        workMatesViewState.getWorkmateName(),
                        workMatesViewState.getWorkmatePhoto())));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}