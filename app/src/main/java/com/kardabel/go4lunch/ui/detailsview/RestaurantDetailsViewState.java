package com.kardabel.go4lunch.ui.detailsview;

import androidx.annotation.DrawableRes;

public class RestaurantDetailsViewState {

    private final String detailsRestaurantName;
    private final String detailsRestaurantAddress;
    private final String detailsPhoto;
    private final String detailsRestaurantNumber;
    private final String detailsWebsite;
    private final String detailsRestaurantId;
    private double rating;
    @DrawableRes
    private final int choseRestaurantButton;
    @DrawableRes
    private final int detailLikeButton;

    public RestaurantDetailsViewState(String detailsRestaurantName,
                                      String detailsRestaurantAddress,
                                      String detailsPhoto,
                                      String detailsRestaurantNumber,
                                      String detailsWebsite,
                                      String detailsRestaurantId,
                                      double rating,
                                      int choseRestaurantButton,
                                      int detailLikeButton) {
        this.detailsRestaurantName = detailsRestaurantName;
        this.detailsRestaurantAddress = detailsRestaurantAddress;
        this.detailsPhoto = detailsPhoto;
        this.detailsRestaurantNumber = detailsRestaurantNumber;
        this.detailsWebsite = detailsWebsite;
        this.detailsRestaurantId = detailsRestaurantId;
        this.rating = rating;
        this.choseRestaurantButton = choseRestaurantButton;
        this.detailLikeButton = detailLikeButton;

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


}
