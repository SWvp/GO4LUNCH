package com.kardabel.go4lunch.ui.detailsview;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.Objects;

public class RestaurantDetailsViewState {

    private final String detailsRestaurantName;
    private final String detailsRestaurantAddress;
    private final String detailsPhoto;
    private final String detailsRestaurantNumber;
    private final String detailsWebsite;
    private final String detailsRestaurantId;
    private final double rating;
    @DrawableRes
    private final int choseRestaurantButton;
    @DrawableRes
    private final int detailLikeButton;
    @ColorRes
    private final int backgroundColor;

    public RestaurantDetailsViewState(String detailsRestaurantName,
                                      String detailsRestaurantAddress,
                                      String detailsPhoto,
                                      String detailsRestaurantNumber,
                                      String detailsWebsite,
                                      String detailsRestaurantId,
                                      double rating,
                                      int choseRestaurantButton,
                                      int detailLikeButton,
                                      int backgroundColor) {
        this.detailsRestaurantName = detailsRestaurantName;
        this.detailsRestaurantAddress = detailsRestaurantAddress;
        this.detailsPhoto = detailsPhoto;
        this.detailsRestaurantNumber = detailsRestaurantNumber;
        this.detailsWebsite = detailsWebsite;
        this.detailsRestaurantId = detailsRestaurantId;
        this.rating = rating;
        this.choseRestaurantButton = choseRestaurantButton;
        this.detailLikeButton = detailLikeButton;
        this.backgroundColor = backgroundColor;

    }

    public String getDetailsRestaurantName() { return detailsRestaurantName; }

    public String getDetailsRestaurantAddress() { return detailsRestaurantAddress; }

    public String getDetailsPhoto() { return detailsPhoto; }

    public String getDetailsRestaurantNumber() { return detailsRestaurantNumber; }

    public String getDetailsWebsite() { return detailsWebsite; }

    public String getDetailsRestaurantId() { return detailsRestaurantId; }

    public double getRating() { return rating; }

    public int getChoseRestaurantButton() { return choseRestaurantButton; }

    public int getDetailLikeButton() { return detailLikeButton; }

    public int getBackgroundColor() { return backgroundColor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantDetailsViewState that = (RestaurantDetailsViewState) o;
        return Double.compare(that.rating, rating) == 0 && choseRestaurantButton == that.choseRestaurantButton && detailLikeButton == that.detailLikeButton && backgroundColor == that.backgroundColor && Objects.equals(detailsRestaurantName, that.detailsRestaurantName) && Objects.equals(detailsRestaurantAddress, that.detailsRestaurantAddress) && Objects.equals(detailsPhoto, that.detailsPhoto) && Objects.equals(detailsRestaurantNumber, that.detailsRestaurantNumber) && Objects.equals(detailsWebsite, that.detailsWebsite) && Objects.equals(detailsRestaurantId, that.detailsRestaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(detailsRestaurantName, detailsRestaurantAddress, detailsPhoto, detailsRestaurantNumber, detailsWebsite, detailsRestaurantId, rating, choseRestaurantButton, detailLikeButton, backgroundColor);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantDetailsViewState{" +
                "detailsRestaurantName='" + detailsRestaurantName + '\'' +
                ", detailsRestaurantAddress='" + detailsRestaurantAddress + '\'' +
                ", detailsPhoto='" + detailsPhoto + '\'' +
                ", detailsRestaurantNumber='" + detailsRestaurantNumber + '\'' +
                ", detailsWebsite='" + detailsWebsite + '\'' +
                ", detailsRestaurantId='" + detailsRestaurantId + '\'' +
                ", rating=" + rating +
                ", choseRestaurantButton=" + choseRestaurantButton +
                ", detailLikeButton=" + detailLikeButton +
                ", backgroundColor=" + backgroundColor +
                '}';
    }
}
