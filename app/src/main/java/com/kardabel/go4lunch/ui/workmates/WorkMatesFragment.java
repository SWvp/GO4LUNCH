package com.kardabel.go4lunch.ui.workmates;

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

import com.kardabel.go4lunch.databinding.RecyclerviewWorkmatesBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.ui.chat.ChatActivity;

import java.util.List;

public class WorkMatesFragment extends Fragment {

    private WorkMatesViewModel workMatesViewModel;
    private RecyclerviewWorkmatesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = RecyclerviewWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();

        WorkMatesRecyclerViewAdapter adapter = new WorkMatesRecyclerViewAdapter();

        binding.workmateRecyclerView.setAdapter(adapter);
        binding.workmateRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.workmateRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));

        // INJECTION OF WORMATES VIEWMODEL
        ViewModelFactory workmatesViewModelFactory = ViewModelFactory.getInstance();
        workMatesViewModel =
                new ViewModelProvider(this, workmatesViewModelFactory).get(WorkMatesViewModel.class);

        // CONFIGURE RECYCLERVIEW
        workMatesViewModel.getWorkmatesViewStateLiveData().observe(getViewLifecycleOwner(), new Observer<List<WorkMatesViewState>>() {
            @Override
            public void onChanged(List<WorkMatesViewState> workMatesViewStates) {
                adapter.setWorkmatesListData(workMatesViewStates);
            }
        });

        adapter.setOnItemClickListener(new WorkMatesRecyclerViewAdapter.OnWorkmateItemClickListener() {
            @Override
            public void onWorkmateItemClick(WorkMatesViewState workMatesViewState) {
                startActivity(ChatActivity.navigate(
                        requireContext(),
                        workMatesViewState.getWorkmateId(),
                        workMatesViewState.getWorkmateName(),
                        workMatesViewState.getWorkmatePhoto()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}