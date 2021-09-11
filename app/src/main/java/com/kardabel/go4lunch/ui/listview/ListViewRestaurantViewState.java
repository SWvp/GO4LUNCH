package com.kardabel.go4lunch.ui.listview;

public class ListViewRestaurantViewState {
    private final String name;
    private final String address;
    private final String avatar;
    private final String distance;
    //private final String foodType;
    private final String openingHours;
    private final double rating;
    private final String placeId;

    public ListViewRestaurantViewState(String name, String address, String avatar, String distance, String openingHours, double rating, String placeId) {
        this.name = name;
        this.address = address;
        this.avatar = avatar;
        this.distance = distance;
        //this.foodType = foodType;
        this.openingHours = openingHours;
        this.rating = rating;
        this.placeId = placeId;
    }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public String getAvatar() { return avatar; }

    public String getDistance() { return distance; }

    //public String getFoodType() { return foodType; }

    public String getOpeningHours() { return openingHours; }

    public double getRating() { return rating; }

    public String getPlaceId() { return placeId; }
}
