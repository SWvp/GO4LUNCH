package com.kardabel.go4lunch.ui.restaurants;

import java.util.Objects;

public class RestaurantsViewState {

    private final String name;
    private final String address;
    private final String photo;
    private final String distance;
    private final String openingHours;
    private final double rating;
    private final String placeId;

    public RestaurantsViewState(String name, String address, String photo, String distance, String openingHours, double rating, String placeId) {
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.distance = distance;
        this.openingHours = openingHours;
        this.rating = rating;
        this.placeId = placeId;
    }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public String getPhoto() { return photo; }

    public String getDistance() { return distance; }

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
                Objects.equals(photo, that.photo) &&
                Objects.equals(distance, that.distance) &&
                Objects.equals(openingHours, that.openingHours) &&
                Objects.equals(placeId, that.placeId);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                address,
                photo,
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
                ", avatar='" + photo + '\'' +
                ", distance='" + distance + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", rating=" + rating +
                ", placeId='" + placeId + '\'' +
                '}';
    }
}

