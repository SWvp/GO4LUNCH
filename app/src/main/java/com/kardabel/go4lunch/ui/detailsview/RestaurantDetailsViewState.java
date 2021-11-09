package com.kardabel.go4lunch.ui.detailsview;

public class RestaurantDetailsViewState {

    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String PHOTO_REFERENCE ="photo?maxwidth=300&photo_reference=";
    private static final String API_KEY ="AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";

    private final String detailsRestaurantName;
    private final String detailsRestaurantAddress;
    private final String detailsPhoto;
    private final String detailsRestaurantNumber;
    private final String detailsWebsite;
    private final String detailsRestaurantId;

    public RestaurantDetailsViewState(String detailsRestaurantName,
                                      String detailsRestaurantAddress,
                                      String detailsPhoto,
                                      String detailsRestaurantNumber,
                                      String detailsWebsite,
                                      String detailsRestaurantId) {
        this.detailsRestaurantName = detailsRestaurantName;
        this.detailsRestaurantAddress = detailsRestaurantAddress;
        this.detailsPhoto = detailsPhoto;
        this.detailsRestaurantNumber = detailsRestaurantNumber;
        this.detailsWebsite = detailsWebsite;
        this.detailsRestaurantId = detailsRestaurantId;

    }

    public String getDetailsRestaurantName() { return detailsRestaurantName; }

    public String getDetailsRestaurantAddress() { return detailsRestaurantAddress; }

    public String getDetailsPhoto() { return detailsPhoto; }

    public String getDetailsRestaurantNumber() { return detailsRestaurantNumber; }

    public String getDetailsWebsite() { return detailsWebsite; }

    public String getDetailsRestaurantId() { return detailsRestaurantId; }

    public static String urlPhotoDetails(RestaurantDetailsViewState restaurantDetailsViewState) {
        if (restaurantDetailsViewState.getDetailsPhoto() != null) {
            String photoReference = restaurantDetailsViewState.getDetailsPhoto();
            return API_URL + PHOTO_REFERENCE + photoReference + "&key=" + API_KEY;
        }
        return "";
    }

}
