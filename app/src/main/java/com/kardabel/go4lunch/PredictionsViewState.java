package com.kardabel.go4lunch;


public class PredictionsViewState {

    private final String predictionDescription;
    private final String predictionPlaceId;

    public PredictionsViewState(String predictionDescription, String predictionPlaceId) {
        this.predictionDescription = predictionDescription;
        this.predictionPlaceId = predictionPlaceId;
    }

    public String getPredictionDescription() { return predictionDescription; }

    public String getPredictionPlaceId() { return predictionPlaceId; }
}
