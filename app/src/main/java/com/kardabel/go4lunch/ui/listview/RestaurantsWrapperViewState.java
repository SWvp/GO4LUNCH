package com.kardabel.go4lunch.ui.listview;

import java.util.List;

public class RestaurantsWrapperViewState {

    // WRAPPER OF RESTAURANTS ITEMS

    private final List<RestaurantItemViewState> itemRestaurant;

    public RestaurantsWrapperViewState(List<RestaurantItemViewState> itemRestaurant) {
        this.itemRestaurant = itemRestaurant;

    }
    public List<RestaurantItemViewState> getItemRestaurant() { return itemRestaurant; }

}
