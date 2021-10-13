package com.kardabel.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.databinding.RecyclerviewWorkmatesBinding;

public class WorkMatesFragment extends Fragment {

    private WorkMatesViewModel mWorkMatesViewModel;
    private RecyclerviewWorkmatesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mWorkMatesViewModel =
                new ViewModelProvider(this).get(WorkMatesViewModel.class);

        binding = RecyclerviewWorkmatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //     final TextView textView = binding.textWorkmates;
        //     mWorkMatesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //         @Override
        //         public void onChanged(@Nullable String s) {
        //             textView.setText(s);
        //         }
        //     });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}