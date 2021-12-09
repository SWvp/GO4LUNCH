package com.kardabel.go4lunch.ui.workmates;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.databinding.ItemWorkmateBinding;

import java.util.ArrayList;
import java.util.List;

public class WorkMatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkMatesRecyclerViewAdapter.ViewHolder> {

    private OnWorkmateItemClickListener onWorkmateItemClickListener;

    private List<WorkMateViewState> workmatesList = new ArrayList<>();

    // INIT THE ADAPTER WITH NEW LIST OF WORKMATES
    @SuppressLint("NotifyDataSetChanged")
    public void setWorkmatesListData(List<WorkMateViewState> workmatesList) {
        this.workmatesList = workmatesList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkmateBinding binding = ItemWorkmateBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ViewHolder(binding);

    }

    // BIND THE VIEW WITH VIEW STATE VAL
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WorkMateViewState workMate = workmatesList.get(position);

        holder
                .viewHolderBinding
                .itemWorkmateDescription
                .setTextColor(workMate.getTextColor());
        holder
                .viewHolderBinding
                .itemWorkmateDescription
                .setText(workMate.getWorkmateDescription());
        Glide.with(holder.viewHolderBinding.itemWorkmateAvatar.getContext())
                .load(workMate.getWorkmatePhoto())
                .circleCrop()
                .into(holder.viewHolderBinding.itemWorkmateAvatar);
        // TODO : if there is a solution to pass styleRes to avoid "if" in view
        if (!workMate.isUserHasDecided()) {
            holder
                    .viewHolderBinding
                    .itemWorkmateDescription
                    .setTypeface(null, Typeface.ITALIC);

        }
        holder.itemView.setOnClickListener(v ->
                onWorkmateItemClickListener.onWorkmateItemClick(workMate));

    }

    @Override
    public int getItemCount() {
        if (workmatesList == null) {
            return 0;
        } else {
            return workmatesList.size();

        }
    }

    // VIEW HOLDER CLASS
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemWorkmateBinding viewHolderBinding;

        public ViewHolder(@NonNull ItemWorkmateBinding itemView) {
            super(itemView.getRoot());
            viewHolderBinding = itemView;

        }
    }

    // WHEN USER CLICK ON A WORKMATE ITEM TO DISPLAY RESTAURANT DETAILS
    public void setOnItemClickListener(OnWorkmateItemClickListener onWorkmateItemClickListener) {
        this.onWorkmateItemClickListener = onWorkmateItemClickListener;

    }

    public interface OnWorkmateItemClickListener {
        void onWorkmateItemClick(WorkMateViewState workMateViewState);

    }
}
