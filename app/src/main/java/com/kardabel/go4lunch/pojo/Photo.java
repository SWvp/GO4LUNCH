package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Photo {

    @SerializedName("width")
    @Expose
    private final Integer photoWidth;

    @SerializedName("height")
    @Expose
    private final Integer photoHeight;

    @SerializedName("html_attributions")
    @Expose
    private final List<String> photoHtmlAttributions;

    @SerializedName("photo_reference")
    @Expose
    private final String photoReference;

    public Photo(Integer photoWidth, Integer photoHeight, List<String> photoHtmlAttributions, String photoReference) {
        this.photoWidth = photoWidth;
        this.photoHeight = photoHeight;
        this.photoHtmlAttributions = photoHtmlAttributions;
        this.photoReference = photoReference;
    }

    public Integer getPhotoWidth() { return photoWidth; }

    public Integer getPhotoHeight() { return photoHeight; }

    public List<String> getPhotoHtmlAttributions() { return photoHtmlAttributions; }

    public String getPhotoReference() { return photoReference; }
}
