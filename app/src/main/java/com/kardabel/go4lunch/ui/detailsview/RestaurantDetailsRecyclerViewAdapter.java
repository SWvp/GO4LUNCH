package com.kardabel.go4lunch.ui.detailsview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.databinding.ItemWorkmateBinding;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantDetailsRecyclerViewAdapter.ViewHolder> {

    private List<RestaurantDetailsWorkmatesViewState> workmatesList= new ArrayList<>();

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

        RestaurantDetailsWorkmatesViewState workMate = workmatesList.get(position);

        holder.viewHolderBinding.itemWorkmateDescription.setText(workMate.getWorkmateName());
        Glide.with(holder.viewHolderBinding.itemWorkmateAvatar.getContext())
                .load(workMate.getWorkmateDetailPhoto())
                .circleCrop()
                .into(holder.viewHolderBinding.itemWorkmateAvatar);

    }

    @Override
    public int getItemCount() {
        if(workmatesList == null){
            return 0;
        } else{
            return workmatesList.size();
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWorkmatesListData(List<RestaurantDetailsWorkmatesViewState> workMates){
        this.workmatesList = workMates;
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemWorkmateBinding viewHolderBinding;
        public ViewHolder(@NonNull ItemWorkmateBinding itemView) {
            super(itemView.getRoot());
            this.viewHolderBinding = itemView;
        }
    }
}
