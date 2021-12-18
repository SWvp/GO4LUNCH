package com.kardabel.go4lunch;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MainActivityYourLunchViewState {

    private final String restaurantId;
    private final int currentUserRestaurantChoiceStatus;

    public MainActivityYourLunchViewState(
            String restaurantId,
            int currentUserRestaurantChoiceStatus) {

        this.restaurantId = restaurantId;
        this.currentUserRestaurantChoiceStatus = currentUserRestaurantChoiceStatus;

    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public int getCurrentUserRestaurantChoiceStatus() {
        return currentUserRestaurantChoiceStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainActivityYourLunchViewState that = (MainActivityYourLunchViewState) o;
        return currentUserRestaurantChoiceStatus == that.currentUserRestaurantChoiceStatus &&
                Objects.equals(restaurantId, that.restaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                restaurantId,
                currentUserRestaurantChoiceStatus);
    }

    @NonNull
    @Override
    public String toString() {
        return "MainActivityYourLunchViewState{" +
                "restaurantId='" + restaurantId + '\'' +
                ", currentUserRestaurantChoiceStatus=" + currentUserRestaurantChoiceStatus +
                '}';
    }
}
