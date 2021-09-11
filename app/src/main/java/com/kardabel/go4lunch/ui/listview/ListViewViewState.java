package com.kardabel.go4lunch.ui.listview;

import java.util.List;

public class ListViewViewState {

    private final List<ListViewRestaurantViewState> itemRestaurant;

    public ListViewViewState(List<ListViewRestaurantViewState> itemRestaurant) {
        this.itemRestaurant = itemRestaurant;

    }
    public List<ListViewRestaurantViewState> getItemRestaurant() { return itemRestaurant; }

}
