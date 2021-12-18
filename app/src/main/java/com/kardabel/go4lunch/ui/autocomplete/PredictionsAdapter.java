package com.kardabel.go4lunch.ui.autocomplete;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kardabel.go4lunch.R;

public class PredictionsAdapter extends ListAdapter<PredictionViewState, PredictionsAdapter.ViewHolder> {

    @NonNull
    private final OnPredictionItemClickedListener listener;

    public PredictionsAdapter(@NonNull OnPredictionItemClickedListener listener) {
        super(new PredictionsAdapterDiffCallBack());
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prediction, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {
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
                @NonNull final PredictionViewState item,
                @NonNull final OnPredictionItemClickedListener listener) {

            predictionText.setText(item.getPredictionDescription());

            layout.setOnClickListener(v -> listener.onPredictionItemClicked(item.getPredictionName()));
        }
    }


    // THIS CLASS ALLOW TO VERIFY THE LIST SUBMIT BY ACTIVITY, IF OLDER, THE LIST IS UPDATED BY NEW ONE
    private static class PredictionsAdapterDiffCallBack extends DiffUtil.ItemCallback<PredictionViewState> {

        @Override
        public boolean areItemsTheSame(
                @NonNull PredictionViewState oldItem,
                @NonNull PredictionViewState newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull PredictionViewState oldItem,
                @NonNull PredictionViewState newItem) {
            return false;
        }
    }

    public interface OnPredictionItemClickedListener {
        void onPredictionItemClicked(String predictionText);

    }
}
