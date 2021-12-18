package com.kardabel.go4lunch.ui.autocomplete;


import androidx.annotation.NonNull;

import java.util.Objects;

public class PredictionViewState {

    private final String predictionDescription;
    private final String predictionPlaceId;
    private final String predictionName;

    public PredictionViewState(
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredictionViewState that = (PredictionViewState) o;
        return Objects.equals(predictionDescription, that.predictionDescription) &&
                Objects.equals(predictionPlaceId, that.predictionPlaceId) &&
                Objects.equals(predictionName, that.predictionName);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                predictionDescription,
                predictionPlaceId,
                predictionName);

    }

    @NonNull
    @Override
    public String toString() {
        return "PredictionViewState{" +
                "predictionDescription='" + predictionDescription + '\'' +
                ", predictionPlaceId='" + predictionPlaceId + '\'' +
                ", predictionName='" + predictionName + '\'' +
                '}';

    }
}
