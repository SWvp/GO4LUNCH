package com.kardabel.go4lunch;


public class PredictionsViewState {

    private final String predictionDescription;
    private final String predictionPlaceId;
    private final String predictionName;

    public PredictionsViewState(
            String predictionDescription,
            String predictionPlaceId,
            String predictionName) {
        this.predictionDescription = predictionDescription;
        this.predictionPlaceId = predictionPlaceId;
        this.predictionName = predictionName;
    }

    public String getPredictionDescription() {
        return predictionDescription;
    }

    public String getPredictionPlaceId() { return predictionPlaceId; }

    public String getPredictionName() { return predictionName; }
}
