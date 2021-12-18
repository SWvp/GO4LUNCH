package com.kardabel.go4lunch.ui.restaurants;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import java.util.Objects;

public class RestaurantsViewState {

    private final int distanceInt;
    private final String name;
    private final String address;
    private final String photo;
    private final String distanceText;
    private final String openingHours;
    private final double rating;
    private final String placeId;
    private final String usersWhoChoseThisRestaurant;
    @ColorRes
    private final int textColor;

    public RestaurantsViewState(
            int distanceInt,
            String name,
            String address,
            String photo,
            String distanceText,
            String openingHours,
            double rating,
            String placeId,
            String usersWhoChoseThisRestaurant,
            int textColor) {

        this.distanceInt = distanceInt;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.distanceText = distanceText;
        this.openingHours = openingHours;
        this.rating = rating;
        this.placeId = placeId;
        this.usersWhoChoseThisRestaurant = usersWhoChoseThisRestaurant;
        this.textColor = textColor;

    }

    public int getDistanceInt() { return distanceInt; }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public String getPhoto() { return photo; }

    public String getDistanceText() { return distanceText; }

    public String getOpeningHours() { return openingHours; }

    public double getRating() { return rating; }

    public String getPlaceId() { return placeId; }

    public String getUsersWhoChoseThisRestaurant() { return usersWhoChoseThisRestaurant; }

    public int getTextColor() { return textColor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsViewState that = (RestaurantsViewState) o;
        return distanceInt == that.distanceInt &&
                Double.compare(that.rating, rating) == 0 &&
                textColor == that.textColor &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address) &&
                Objects.equals(photo, that.photo) &&
                Objects.equals(distanceText, that.distanceText) &&
                Objects.equals(openingHours, that.openingHours) &&
                Objects.equals(placeId, that.placeId) &&
                Objects.equals(usersWhoChoseThisRestaurant, that.usersWhoChoseThisRestaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                distanceInt,
                name,
                address,
                photo,
                distanceText,
                openingHours,
                rating,
                placeId,
                usersWhoChoseThisRestaurant,
                textColor);

    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantsViewState{" +
                "distanceInt=" + distanceInt +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", photo='" + photo + '\'' +
                ", distanceText='" + distanceText + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", rating=" + rating +
                ", placeId='" + placeId + '\'' +
                ", usersWhoChoseThisRestaurant='" + usersWhoChoseThisRestaurant + '\'' +
                ", textColor=" + textColor +
                '}';

    }
}

