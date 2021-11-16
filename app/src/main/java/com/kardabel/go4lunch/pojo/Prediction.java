package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Prediction {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("types")
    @Expose
    private List<String> types = null;


    public Prediction(String description,
                      String placeId,
                      String reference,
                      List<String> types) {
        this.description = description;
        this.placeId = placeId;
        this.reference = reference;
        this.types = types;
    }

}
