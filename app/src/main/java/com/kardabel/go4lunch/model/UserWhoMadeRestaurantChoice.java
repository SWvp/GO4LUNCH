package com.kardabel.go4lunch.model;

import java.util.Objects;

public class UserWhoMadeRestaurantChoice {

    private String restaurantId;
    private String restaurantName;
    private String userId;

    public UserWhoMadeRestaurantChoice() {
    }

    public UserWhoMadeRestaurantChoice(String restaurantId, String restaurantName, String userId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWhoMadeRestaurantChoice that = (UserWhoMadeRestaurantChoice) o;
        return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(restaurantName, that.restaurantName) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, restaurantName, userId);
    }

    @Override
    public String toString() {
        return "WorkmateWhoMadeRestaurantChoice{" +
                "restaurantId='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
