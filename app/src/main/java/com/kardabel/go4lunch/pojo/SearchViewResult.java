package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;


public class SearchViewResult {

    @SerializedName("result")
    private final Restaurant result;


    public SearchViewResult(Restaurant result) { this.result = result; }

    public Restaurant getSearchViewResult() { return result; }
}
