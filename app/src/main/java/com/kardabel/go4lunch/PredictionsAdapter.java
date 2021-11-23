package com.kardabel.go4lunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PredictionsAdapter extends BaseAdapter {

    private List<PredictionsViewState> predictions= new ArrayList<>();
    private Context context;


    @Override
    public int getCount() {
        if(predictions == null){ return 0; }
        else{ return predictions.size(); }
    }

    @Override
    public Object getItem(int position) {

        return predictions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_prediction, parent, false);

        }

        PredictionsViewState currentPrediction = (PredictionsViewState) getItem(position);

        TextView restaurantName = convertView.findViewById(R.id.restaurant_name);
        restaurantName.setText(currentPrediction.getPredictionDescription());

        return convertView;


    }

    // SET THE ADAPTER WITH PREDICTIONS LIST RETRIEVE FROM THE VIEW STATE
    public void setPredictionsItemList(List<PredictionsViewState> predictions, Context context) {
        this.predictions = predictions;
        this.context = context;
    }
}
