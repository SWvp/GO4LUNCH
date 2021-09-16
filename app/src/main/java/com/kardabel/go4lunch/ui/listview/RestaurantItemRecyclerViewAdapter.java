package com.kardabel.go4lunch.ui.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kardabel.go4lunch.databinding.ItemRestaurantBinding;
import com.kardabel.go4lunch.pojo.RestaurantDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RestaurantItemRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantItemRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<RestaurantItemViewState> restaurantList = new ArrayList<>();
    private OnRestaurantItemClickListener onRestaurantItemClickListener;
    private OnSearchViewQueryListener onSearchViewQueryListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RestaurantItemViewState restaurant = restaurantList.get(position);

        holder.viewHolderBinding.itemListviewRestaurantName.setText(restaurant.getName());
        holder.viewHolderBinding.itemListviewAddress.setText(restaurant.getAddress());
        holder.viewHolderBinding.itemListviewOpeningHour.setText(restaurant.getOpeningHours());
        holder.viewHolderBinding.itemListviewDistance.setText(restaurant.getDistance());

        String urlPhoto = RestaurantItemViewState.urlPhoto(restaurant);
        Glide.with(holder.viewHolderBinding.itemListviewRestaurantPicture.getContext())
                .load(urlPhoto)
                .into(holder.viewHolderBinding.itemListviewRestaurantPicture);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestaurantItemClickListener.onRestaurantItemClick(restaurant);

            }
        });
    }


    @Override
    public int getItemCount() {
        if(restaurantList == null){ return 0; }
        else{return restaurantList.size();}

    }

    public void setRestaurantListData(List<RestaurantItemViewState> restaurantList){
        this.restaurantList = restaurantList;
        notifyDataSetChanged();

    }

    @Override
    public Filter getFilter() {
        return filter;

    }

    // FILTER RESTAURANTS ITEM WITH SEARCHVIEW QUERY
    Filter filter = new Filter() {

        // RUN ON BACKGROUND THREAD
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RestaurantItemViewState> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(restaurantList);
            } else{
                for (RestaurantItemViewState restaurant : restaurantList){
                    if (restaurant.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(restaurant);

                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;

        }

        // RUN ON UI THREAD
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            restaurantList.clear();
            restaurantList.addAll((Collection<? extends RestaurantItemViewState>) results.values);
            notifyDataSetChanged();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRestaurantBinding viewHolderBinding;
        public ViewHolder(@NonNull ItemRestaurantBinding itemView) {
            super(itemView.getRoot());
            viewHolderBinding = itemView;

        }
    }

    public void setOnItemClickListener(OnRestaurantItemClickListener onRestaurantItemClickListener){
        this.onRestaurantItemClickListener = onRestaurantItemClickListener;

    }

    // WHEN USER CLICK ON A RESTAURANT ITEM TO DISPLAY RESTAURANT DETAILS
    public interface OnRestaurantItemClickListener{
        void onRestaurantItemClick (RestaurantItemViewState restaurantItemViewState);

    }

    public interface OnSearchViewQueryListener{
        void onSearchViewQuery (String newText);

    }
}
