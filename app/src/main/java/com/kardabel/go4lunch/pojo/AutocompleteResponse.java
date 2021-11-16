package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutocompleteResponse {

    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions = null;
    @SerializedName("status")
    @Expose
    private String status;


    public AutocompleteResponse(List<Prediction> predictions, String status) {
        this.predictions = predictions;
        this.status = status;
    }


    public List<Prediction> getPredictions() {
        return predictions;
    }

    public String getStatus() {
        return status;
    }

}
