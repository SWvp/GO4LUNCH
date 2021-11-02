package com.kardabel.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.databinding.ItemWorkmateBinding;
import com.kardabel.go4lunch.ui.restaurants.RestaurantsViewState;

import java.util.ArrayList;
import java.util.List;

public class WorkMatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkMatesRecyclerViewAdapter.ViewHolder> {

    private List<WorkMatesViewState> workmatesList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkmateBinding binding = ItemWorkmateBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WorkMatesViewState workMate = workmatesList.get(position);

        holder.viewHolderBinding.itemWorkmateDescription.setText(workMate.getWorkmateDescription());

        Glide.with(holder.viewHolderBinding.itemWorkmateAvatar.getContext())
                .load(workMate.getWorkmatePhoto())
                .circleCrop()
                .into(holder.viewHolderBinding.itemWorkmateAvatar);

    }

    @Override
    public int getItemCount() {
        if(workmatesList == null){ return 0; }
        else{return workmatesList.size();}

    }

    public void setWorkmatesListData(List<WorkMatesViewState> workmatesList){
        this.workmatesList = workmatesList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemWorkmateBinding viewHolderBinding;
        public ViewHolder(@NonNull ItemWorkmateBinding itemView) {
            super(itemView.getRoot());
            viewHolderBinding = itemView;

        }
    }
}
