package com.kardabel.go4lunch.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class UserWhoMadeRestaurantChoice {

    private String restaurantId;
    private String restaurantName;
    private String userId;
    private String restaurantAddress;

    public UserWhoMadeRestaurantChoice() {
    }

    public UserWhoMadeRestaurantChoice(
            String restaurantId,
            String restaurantName,
            String userId,
            String restaurantAddress) {

        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.userId = userId;
        this.restaurantAddress = restaurantAddress;
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

    public String getUserId() {
        return userId;
    }

    public String getRestaurantAddress() { return restaurantAddress; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWhoMadeRestaurantChoice that = (UserWhoMadeRestaurantChoice) o;
        return Objects.equals(restaurantId, that.restaurantId) &&
                Objects.equals(restaurantName, that.restaurantName) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(restaurantAddress, that.restaurantAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                restaurantId,
                restaurantName,
                userId,
                restaurantAddress);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserWhoMadeRestaurantChoice{" +
                "restaurantId='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", userId='" + userId + '\'' +
                ", restaurantAddress='" + restaurantAddress + '\'' +
                '}';
    }
}
