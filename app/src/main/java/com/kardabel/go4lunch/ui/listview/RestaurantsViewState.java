package com.kardabel.go4lunch.ui.listview;

import java.util.Objects;

public class RestaurantsViewState {

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

    public RestaurantsViewState(String name, String address, String avatar, String distance, String openingHours, double rating, String placeId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsViewState that = (RestaurantsViewState) o;
        return Double.compare(that.rating, rating) == 0 &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address) &&
                Objects.equals(avatar, that.avatar) &&
                Objects.equals(distance, that.distance) &&
                Objects.equals(openingHours, that.openingHours) &&
                Objects.equals(placeId, that.placeId);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                address,
                avatar,
                distance,
                openingHours,
                rating,
                placeId);

    }

    @Override
    public String toString() {
        return "RestaurantsViewState{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", avatar='" + avatar + '\'' +
                ", distance='" + distance + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", rating=" + rating +
                ", placeId='" + placeId + '\'' +
                '}';
    }

    public static String urlPhoto(RestaurantsViewState restaurantsViewState) {
        if (restaurantsViewState.getAvatar() != null) {
            String photoReference = restaurantsViewState.getAvatar();
            return API_URL + PHOTO_REFERENCE + photoReference + "&key=" + API_KEY;
        }
        return "";
    }


}

