package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class RestaurantSearch {

    // TEMPLATE CLASS FOR RESTAURANT FIRST DATA WE ARE GOING TO PARSE

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("name")
    @Expose
    private final String restaurantName;

    @SerializedName("vicinity")
    @Expose
    private final String restaurantAddress;

    @SerializedName("photos")
    @Expose
    private final List<Photo> restaurantPhotos;

    @SerializedName("geometry")
    @Expose
    private final Geometry restaurantGeometry;

    @SerializedName("opening_hours")
    @Expose
    private final OpeningHours openingHours;

    @SerializedName("rating")
    @Expose
    private final double rating;

    @SerializedName("user_ratings_total")
    @Expose
    private final int totalRatings;


    public RestaurantSearch(String placeId,
                            String restaurantName,
                            String restaurantAddress,
                            List<Photo> restaurantPhotos,
                            Geometry restaurantGeometry,
                            OpeningHours openingHours,
                            double rating,
                            int totalRatings) {
        this.placeId = placeId;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhotos = restaurantPhotos;
        this.restaurantGeometry = restaurantGeometry;
        this.openingHours = openingHours;
        this.rating = rating;
        this.totalRatings = totalRatings;

    }


    // GETTERS
    public String getPlaceId() { return placeId; }

    public String getRestaurantName() { return restaurantName; }

    public String getRestaurantAddress() { return restaurantAddress; }

    public List<Photo> getRestaurantPhotos() { return restaurantPhotos; }

    public Geometry getRestaurantGeometry() { return restaurantGeometry; }

    public OpeningHours getOpeningHours() { return openingHours; }

    public double getRating() { return rating; }

    public int getTotalRatings() { return totalRatings; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantSearch that = (RestaurantSearch) o;
        return Double.compare(that.rating, rating) == 0 &&
                totalRatings == that.totalRatings &&
                Objects.equals(placeId, that.placeId) &&
                Objects.equals(restaurantName, that.restaurantName) &&
                Objects.equals(restaurantAddress, that.restaurantAddress) &&
                Objects.equals(restaurantPhotos, that.restaurantPhotos) &&
                Objects.equals(restaurantGeometry, that.restaurantGeometry) &&
                Objects.equals(openingHours, that.openingHours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                placeId,
                restaurantName,
                restaurantAddress,
                restaurantPhotos,
                restaurantGeometry,
                openingHours,
                rating,
                totalRatings);
    }
}
