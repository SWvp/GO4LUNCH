package com.kardabel.go4lunch.ui.workmates;

import java.util.Objects;

public class WorkMatesViewState {

    private final String workmateDescription;
    private final String workmatePhoto;
    private final String workmateRestaurant;
    private boolean gotRestaurant;

    public WorkMatesViewState(String workmateDescription, String workmatePhoto, String workmateRestaurant, boolean gotRestaurant) {
        this.workmateDescription = workmateDescription;
        this.workmatePhoto = workmatePhoto;
        this.workmateRestaurant = workmateRestaurant;
        this.gotRestaurant = gotRestaurant;
    }

    public String getWorkmateDescription() {
        return workmateDescription;
    }

    public String getWorkmatePhoto() {
        return workmatePhoto;
    }

    public String getWorkmateRestaurant() {
        return workmateRestaurant;
    }

    public boolean isUserGotRestaurant() {
        return gotRestaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkMatesViewState that = (WorkMatesViewState) o;
        return Objects.equals(workmateDescription, that.workmateDescription)
                && Objects.equals(workmatePhoto, that.workmatePhoto)
                && Objects.equals(workmateRestaurant, that.workmateRestaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                workmateDescription,
                workmatePhoto,
                workmateRestaurant);
    }

    @Override
    public String toString() {
        return "WorkMatesViewState{" +
                "workmateName='" + workmateDescription + '\'' +
                ", workmatePhoto='" + workmatePhoto + '\'' +
                ", workmateRestaurant='" + workmateRestaurant + '\'' +
                '}';
    }
}
