package com.kardabel.go4lunch.ui.listview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kardabel.go4lunch.databinding.ItemListviewBinding;

import java.util.ArrayList;
import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private List<ListViewRestaurantViewState> restaurantList = new ArrayList<>();
    private OnRestaurantItemClickListener listener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListviewBinding binding = ItemListviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListViewRestaurantViewState restaurant = restaurantList.get(position);

        holder.viewHolderBinding.itemListviewRestaurantName.setText(restaurant.getName());
        holder.viewHolderBinding.itemListviewAddress.setText(restaurant.getAddress());
        holder.viewHolderBinding.itemListviewOpeningHour.setText(restaurant.getOpeningHours());
        holder.viewHolderBinding.itemListviewDistance.setText(restaurant.getDistance());
        //holder.viewHolderBinding.itemListviewRestaurantPicture.setImageResource(restaurant.getAvatar());

    }


    @Override
    public int getItemCount() {
        if(restaurantList == null){ return 0; }
        else{return restaurantList.size();}
    }

    public void setRestaurantListData(List<ListViewRestaurantViewState> restaurantList){
        this.restaurantList = restaurantList;
        notifyDataSetChanged();

    }





    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListviewBinding viewHolderBinding;
        public ViewHolder(@NonNull ItemListviewBinding itemView) {
            super(itemView.getRoot());
            viewHolderBinding = itemView;

        }
    }

    // WHEN USER CLICK ON A RESTAURANT TO DISPLAY DETAILS
    public interface OnRestaurantItemClickListener{
        void onRestaurantItemClick (ListViewViewState restaurant);

    }

}
