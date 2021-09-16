package com.kardabel.go4lunch.ui.listview;

public class RestaurantItemViewState {

    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String PHOTO_REFERENCE ="photo?maxwidth=300&photo_reference=";
    private static final String API_KEY ="AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";

    private final String name;
    private final String address;
    private final String avatar;
    private final String distance;
    //private final String foodType;
    private final String openingHours;
    private final double rating;
    private final String placeId;

    public RestaurantItemViewState(String name, String address, String avatar, String distance, String openingHours, double rating, String placeId) {
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

    public static String urlPhoto(RestaurantItemViewState restaurantItemViewState) {
        if (restaurantItemViewState.getAvatar() != null) {
            String photoReference = restaurantItemViewState.getAvatar();
            return API_URL + PHOTO_REFERENCE + photoReference + "&key=" + API_KEY;
        }
        return "";
    }
}

