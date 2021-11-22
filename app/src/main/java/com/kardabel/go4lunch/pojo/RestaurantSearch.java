package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class RestaurantSearch {

    // TEMPLATE CLASS FOR RESTAURANT FIRST DATA WE ARE GOING TO PARSE

    @SerializedName("place_id")
    @Expose
    private String restaurantId;

    @SerializedName("name")
    @Expose
    private String restaurantName;

    @SerializedName("vicinity")
    @Expose
    private String restaurantAddress;

    @SerializedName("photos")
    @Expose
    private List<Photo> restaurantPhotos;

    @SerializedName("geometry")
    @Expose
    private Geometry restaurantGeometry;

    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("user_ratings_total")
    @Expose
    private int totalRatings;

    @SerializedName("permanently_closed")
    @Expose
    private boolean permanentlyClosed;

    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;

    @SerializedName("website")
    @Expose
    private String website;

    public RestaurantSearch(){

    }

    public RestaurantSearch(String placeID){
        this.restaurantId = placeID;
    }


    public RestaurantSearch(String restaurantId,
                            String restaurantName,
                            String restaurantAddress,
                            List<Photo> restaurantPhotos,
                            Geometry restaurantGeometry,
                            OpeningHours openingHours,
                            double rating,
                            int totalRatings,
                            boolean permanentlyClosed,
                            String formattedPhoneNumber,
                            String website) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhotos = restaurantPhotos;
        this.restaurantGeometry = restaurantGeometry;
        this.openingHours = openingHours;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.permanentlyClosed = permanentlyClosed;
        this.formattedPhoneNumber = formattedPhoneNumber;
        this.website = website;

    }


    // GETTERS
    public String getRestaurantId() { return restaurantId; }

    public String getRestaurantName() { return restaurantName; }

    public String getRestaurantAddress() { return restaurantAddress; }

    public List<Photo> getRestaurantPhotos() { return restaurantPhotos; }

    public Geometry getRestaurantGeometry() { return restaurantGeometry; }

    public OpeningHours getOpeningHours() { return openingHours; }

    public double getRating() { return rating; }

    public boolean isPermanentlyClosed() {
        return permanentlyClosed;
    }

    public int getTotalRatings() { return totalRatings; }

    public String getFormattedPhoneNumber() { return formattedPhoneNumber; }

    public String getWebsite() { return website; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantSearch that = (RestaurantSearch) o;
        return Double.compare(that.rating, rating) == 0 &&
                totalRatings == that.totalRatings &&
                permanentlyClosed == that.permanentlyClosed &&
                Objects.equals(restaurantId, that.restaurantId) &&
                Objects.equals(restaurantName, that.restaurantName) &&
                Objects.equals(restaurantAddress, that.restaurantAddress) &&
                Objects.equals(restaurantPhotos, that.restaurantPhotos) &&
                Objects.equals(restaurantGeometry, that.restaurantGeometry) &&
                Objects.equals(openingHours, that.openingHours) &&
                Objects.equals(formattedPhoneNumber, that.formattedPhoneNumber) &&
                Objects.equals(website, that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                restaurantId,
                restaurantName,
                restaurantAddress,
                restaurantPhotos,
                restaurantGeometry,
                openingHours,
                rating,
                totalRatings,
                permanentlyClosed,
                formattedPhoneNumber,
                website);
    }

    @Override
    public String toString() {
        return "RestaurantSearch{" +
                "restaurantId='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantAddress='" + restaurantAddress + '\'' +
                ", restaurantPhotos=" + restaurantPhotos +
                ", restaurantGeometry=" + restaurantGeometry +
                ", openingHours=" + openingHours +
                ", rating=" + rating +
                ", totalRatings=" + totalRatings +
                ", permanentlyClosed=" + permanentlyClosed +
                ", formattedPhoneNumber='" + formattedPhoneNumber + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
