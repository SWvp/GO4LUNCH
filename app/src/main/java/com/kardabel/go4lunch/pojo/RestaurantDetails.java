package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class
RestaurantDetails {

    // TEMPLATE CLASS FOR RESTAURANT DETAILS DATA WE ARE GOING TO PARSE

    @SerializedName("place_id")
    private final String placeId;

    @SerializedName("name")
    private final String restaurantName;

    @SerializedName("formatted_phone_number")
    private final String restaurantNumber;

    @SerializedName("vicinity")
    private final String restaurantAddress;

    @SerializedName("photos")
    private final List<Photo> restaurantPhotos;

    @SerializedName("geometry")
    private final Geometry restaurantGeometry;

    @SerializedName("opening_hours")
    private final OpeningHours openingHours;

    @SerializedName("website")
    private final String website;

    @SerializedName("rating")
    private final double rating;

    @SerializedName("user_ratings_total")
    private final int totalRatings;


    public RestaurantDetails(String placeId, String restaurantName, String restaurantNumber, String restaurantAddress, List<Photo> restaurantPhotos, Geometry restaurantGeometry, OpeningHours openingHours, String website, double rating, int totalRatings) {
        this.placeId = placeId;
        this.restaurantName = restaurantName;
        this.restaurantNumber = restaurantNumber;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhotos = restaurantPhotos;
        this.restaurantGeometry = restaurantGeometry;
        this.openingHours = openingHours;
        this.website = website;
        this.rating = rating;
        this.totalRatings = totalRatings;

    }


    // GETTERS
    public String getPlaceId() { return placeId; }

    public String getRestaurantName() { return restaurantName; }

    public String getRestaurantNumber() { return restaurantNumber; }

    public String getRestaurantAddress() { return restaurantAddress; }

    public List<Photo> getRestaurantPhotos() { return restaurantPhotos; }

    public Geometry getRestaurantGeometry() { return restaurantGeometry; }

    public OpeningHours getOpeningHours() { return openingHours; }

    public String getWebsite() { return website; }

    public double getRating() { return rating; }

    public int getTotalRatings() { return totalRatings; }

}
