package com.kardabel.go4lunch.ui.restaurants;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.databinding.ItemRestaurantBinding;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantsRecyclerViewAdapter.ViewHolder> {

    private List<RestaurantsViewState> restaurantList = new ArrayList<>();
    private OnRestaurantItemClickListener onRestaurantItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RestaurantsViewState restaurant = restaurantList.get(position);

        holder.viewHolderBinding.itemListviewRestaurantName.setText(restaurant.getName());
        holder.viewHolderBinding.itemListviewAddress.setText(restaurant.getAddress());
        holder.viewHolderBinding.itemListviewOpeningHour.setText(restaurant.getOpeningHours());
        holder.viewHolderBinding.itemListviewOpeningHour.setTextColor(restaurant.getTextColor());
        holder.viewHolderBinding.itemListviewDistance.setText(restaurant.getDistanceText());
        holder.viewHolderBinding.ratingBar.setRating((float) restaurant.getRating());
        holder.viewHolderBinding.itemListviewInterestedWorkmates.setText(restaurant.getUsersWhoChoseThisRestaurant());
        Glide.with(holder.viewHolderBinding.itemListviewRestaurantPicture.getContext())
                .load(restaurant.getPhoto())
                .into(holder.viewHolderBinding.itemListviewRestaurantPicture);

        holder.itemView.setOnClickListener(v -> onRestaurantItemClickListener.onRestaurantItemClick(restaurant));
    }

    @Override
    public int getItemCount() {
        if (restaurantList == null) {
            return 0;
        } else {
            return restaurantList.size();
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRestaurantListData(List<RestaurantsViewState> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemRestaurantBinding viewHolderBinding;

        public ViewHolder(@NonNull ItemRestaurantBinding itemView) {
            super(itemView.getRoot());
            viewHolderBinding = itemView;

        }
    }

    public void setOnItemClickListener(OnRestaurantItemClickListener onRestaurantItemClickListener) {
        this.onRestaurantItemClickListener = onRestaurantItemClickListener;

    }

    // WHEN USER CLICK ON A RESTAURANT ITEM TO DISPLAY RESTAURANT DETAILS
    public interface OnRestaurantItemClickListener {
        void onRestaurantItemClick(RestaurantsViewState restaurantsViewState);

    }
}
