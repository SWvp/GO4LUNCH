package com.kardabel.go4lunch.ui.restaurants;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class RestaurantsWrapperViewState {

    // WRAPPER OF RESTAURANTS ITEMS
    private final List<RestaurantsViewState> itemRestaurant;

    public RestaurantsWrapperViewState(List<RestaurantsViewState> itemRestaurant) {
        this.itemRestaurant = itemRestaurant;

    }
    public List<RestaurantsViewState> getItemRestaurant() { return itemRestaurant; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsWrapperViewState that = (RestaurantsWrapperViewState) o;
        return Objects.equals(itemRestaurant, that.itemRestaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemRestaurant);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantsWrapperViewState{" +
                "itemRestaurant=" + itemRestaurant +
                '}';
    }
}
