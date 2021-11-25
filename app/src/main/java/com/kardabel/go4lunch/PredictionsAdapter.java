package com.kardabel.go4lunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PredictionsAdapter extends
        ListAdapter<PredictionsViewState,
        PredictionsAdapter.ViewHolder> {

    @NonNull
    private final OnPredictionItemClickedListener listener;

    public PredictionsAdapter(@NonNull OnPredictionItemClickedListener listener) {
        super(new PredictionsAdapterDiffCallBack());
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_prediction, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {
        holder.bind(getItem(position), listener);

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup layout;
        private final TextView predictionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.prediction_item);
            predictionText = itemView.findViewById(R.id.prediction_text);
        }

        public void bind(
                @NonNull final PredictionsViewState item,
                @NonNull final OnPredictionItemClickedListener listener) {

            predictionText.setText(item.getPredictionDescription());

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPredictionItemClicked(item.getPredictionName());

                }
            });
        }
    }



    // THIS CLASS ALLOW TO VERIFY THE LIST SUBMIT BY ACTIVITY, IF OLDER, THE LIST IS UPDATED BY NEW ONE
    private static class PredictionsAdapterDiffCallBack extends DiffUtil.ItemCallback<PredictionsViewState>{

        @Override
        public boolean areItemsTheSame(
                @NonNull PredictionsViewState oldItem,
                @NonNull PredictionsViewState newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull PredictionsViewState oldItem,
                @NonNull PredictionsViewState newItem) {
            return false;
        }
    }



    public interface OnPredictionItemClickedListener{
        void onPredictionItemClicked(String predictionText);

    }
}
