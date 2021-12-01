package com.kardabel.go4lunch.ui.workmates;

import java.util.Objects;

public class WorkMatesViewState {

    private final String workmateName;
    private final String workmateDescription;
    private final String workmatePhoto;
    private final String workmateRestaurant;
    private final String workmateId;
    private boolean gotRestaurant;

    public WorkMatesViewState(String workmateName, String workmateDescription, String workmatePhoto, String workmateRestaurant,String workmateId, boolean gotRestaurant) {
        this.workmateName = workmateName;
        this.workmateDescription = workmateDescription;
        this.workmatePhoto = workmatePhoto;
        this.workmateRestaurant = workmateRestaurant;
        this.gotRestaurant = gotRestaurant;
        this.workmateId = workmateId;

    }

    public String getWorkmateName() { return workmateName; }

    public String getWorkmateDescription() {
        return workmateDescription;
    }

    public String getWorkmatePhoto() {
        return workmatePhoto;
    }

    public String getWorkmateRestaurant() {
        return workmateRestaurant;
    }

    public String getWorkmateId() { return workmateId; }

    public boolean isUserHasDecided() {
        return gotRestaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkMatesViewState that = (WorkMatesViewState) o;
        return gotRestaurant == that.gotRestaurant && Objects.equals(workmateName, that.workmateName) && Objects.equals(workmateDescription, that.workmateDescription) && Objects.equals(workmatePhoto, that.workmatePhoto) && Objects.equals(workmateRestaurant, that.workmateRestaurant) && Objects.equals(workmateId, that.workmateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateName, workmateDescription, workmatePhoto, workmateRestaurant, workmateId, gotRestaurant);
    }

    @Override
    public String toString() {
        return "WorkMatesViewState{" +
                "workmateName='" + workmateName + '\'' +
                ", workmateDescription='" + workmateDescription + '\'' +
                ", workmatePhoto='" + workmatePhoto + '\'' +
                ", workmateRestaurant='" + workmateRestaurant + '\'' +
                ", workmateId='" + workmateId + '\'' +
                ", gotRestaurant=" + gotRestaurant +
                '}';
    }
}
