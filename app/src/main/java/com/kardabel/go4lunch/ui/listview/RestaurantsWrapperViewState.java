package com.kardabel.go4lunch.ui.listview;

import java.util.List;

public class RestaurantsWrapperViewState {

    // WRAPPER OF RESTAURANTS ITEMS

    private final List<RestaurantsViewState> itemRestaurant;

    public RestaurantsWrapperViewState(List<RestaurantsViewState> itemRestaurant) {
        this.itemRestaurant = itemRestaurant;

    }
    public List<RestaurantsViewState> getItemRestaurant() { return itemRestaurant; }

}
