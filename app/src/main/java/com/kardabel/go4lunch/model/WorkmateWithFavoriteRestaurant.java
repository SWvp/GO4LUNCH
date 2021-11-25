package com.kardabel.go4lunch.model;

public class WorkmateWithFavoriteRestaurant {

    private String restaurantId;
    private String restaurantName;
    private String userId;

    public WorkmateWithFavoriteRestaurant() {
    }

    public WorkmateWithFavoriteRestaurant(String restaurantId, String restaurantName, String userId) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
