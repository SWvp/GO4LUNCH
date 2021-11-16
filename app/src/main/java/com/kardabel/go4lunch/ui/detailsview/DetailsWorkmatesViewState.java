package com.kardabel.go4lunch.ui.detailsview;

import java.util.Objects;

public class DetailsWorkmatesViewState {

    private final String workmateName;
    private final String workmateDetailPhoto;

    public DetailsWorkmatesViewState(String workmateName, String workmateDetailPhoto) {
        this.workmateName = workmateName;
        this.workmateDetailPhoto = workmateDetailPhoto;
    }

    public String getWorkmateName() {
        return workmateName;
    }

    public String getWorkmateDetailPhoto() {
        return workmateDetailPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailsWorkmatesViewState that = (DetailsWorkmatesViewState) o;
        return Objects.equals(
                workmateName, that.workmateName) &&
                Objects.equals(workmateDetailPhoto, that.workmateDetailPhoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                workmateName,
                workmateDetailPhoto);
    }

    @Override
    public String toString() {
        return "DetailsWorkmatesViewState{" +
                "workmateDescription='" + workmateName + '\'' +
                ", workmatePhoto='" + workmateDetailPhoto + '\'' +
                '}';
    }
}
