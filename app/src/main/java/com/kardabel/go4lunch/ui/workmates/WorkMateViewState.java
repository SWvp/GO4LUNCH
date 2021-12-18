package com.kardabel.go4lunch.ui.workmates;


import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import java.util.Objects;

public class WorkMateViewState {

    private final String workmateName;
    private final String workmateDescription;
    private final String workmatePhoto;
    private final String workmateId;
    private final boolean gotRestaurant;
    @ColorRes
    private final int textColor;

    public WorkMateViewState(
            String workmateName,
            String workmateDescription,
            String workmatePhoto,
            String workmateId,
            boolean gotRestaurant,
            int textColor) {

        this.workmateName = workmateName;
        this.workmateDescription = workmateDescription;
        this.workmatePhoto = workmatePhoto;
        this.workmateId = workmateId;
        this.gotRestaurant = gotRestaurant;
        this.textColor = textColor;

    }

    public String getWorkmateName() {
        return workmateName;
    }

    public String getWorkmateDescription() {
        return workmateDescription;
    }

    public String getWorkmatePhoto() {
        return workmatePhoto;
    }

    public String getWorkmateId() {
        return workmateId;
    }

    public boolean isUserHasDecided() {
        return gotRestaurant;
    }

    public int getTextColor() {
        return textColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkMateViewState that = (WorkMateViewState) o;
        return gotRestaurant == that.gotRestaurant &&
                textColor == that.textColor &&
                Objects.equals(workmateName, that.workmateName) &&
                Objects.equals(workmateDescription, that.workmateDescription) &&
                Objects.equals(workmatePhoto, that.workmatePhoto) &&
                Objects.equals(workmateId, that.workmateId);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                workmateName,
                workmateDescription,
                workmatePhoto,
                workmateId,
                gotRestaurant,
                textColor);

    }

    @NonNull
    @Override
    public String toString() {
        return "WorkMateViewState{" +
                "workmateName='" + workmateName + '\'' +
                ", workmateDescription='" + workmateDescription + '\'' +
                ", workmatePhoto='" + workmatePhoto + '\'' +
                ", workmateId='" + workmateId + '\'' +
                ", gotRestaurant=" + gotRestaurant +
                ", textColor=" + textColor +
                '}';

    }
}
