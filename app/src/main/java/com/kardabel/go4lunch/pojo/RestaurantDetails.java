package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class RestaurantDetails {

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("opening_hours")
    @Expose
    private final OpeningHours openingHours;

    @SerializedName("formatted_phone_number")
    @Expose
    private String formatted_phone_number;

    @SerializedName("website")
    @Expose
    private String website;

    public RestaurantDetails(String placeId,
                             OpeningHours openingHours,
                             String formatted_phone_number,
                             String website) {
        this.placeId = placeId;
        this.openingHours = openingHours;
        this.formatted_phone_number = formatted_phone_number;
        this.website = website;
    }

    public String getPlaceId() { return placeId; }

    public OpeningHours getOpeningHours() { return openingHours; }

    public String getFormatted_phone_number() { return formatted_phone_number; }

    public String getWebsite() { return website; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantDetails that = (RestaurantDetails) o;
        return Objects.equals(placeId, that.placeId) &&
                Objects.equals(openingHours, that.openingHours) &&
                Objects.equals(formatted_phone_number, that.formatted_phone_number) &&
                Objects.equals(website, that.website);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                placeId,
                openingHours,
                formatted_phone_number,
                website);

    }

    @Override
    public String toString() {
        return "RestaurantDetails{" +
                "placeId='" + placeId + '\'' +
                ", openingHours=" + openingHours +
                ", formatted_phone_number='" + formatted_phone_number + '\'' +
                ", website='" + website + '\'' +
                '}';

    }
}
