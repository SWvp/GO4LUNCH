package com.kardabel.go4lunch.ui.restaurants;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Periods;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantsViewModel extends ViewModel {

    private final MediatorLiveData<RestaurantsWrapperViewState> restaurantsWrapperViewStateMutableLiveData = new MediatorLiveData<>();

    private final Application application;
    private final Clock clock;

    //private static final String googleMapApiKey = BuildConfig.
    public RestaurantsViewModel(
                                @NonNull Application application,
                                @NonNull LocationRepository locationRepository,
                                @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase,
                                @NonNull RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase,
                                @NonNull Clock clock
    ){

        this.clock = clock;
        this.application = application;


        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<List<RestaurantDetailsResult>> restaurantsDetailsResultLiveData = restaurantDetailsResultsUseCase.getPlaceDetailsResultLiveData();
        LiveData<NearbySearchResults> nearbySearchResultsLiveData = nearbySearchResultsUseCase.getNearbySearchResultsLiveData();

        // OBSERVERS

        restaurantsWrapperViewStateMutableLiveData.addSource(locationLiveData, location ->
                combine(nearbySearchResultsLiveData.getValue(), restaurantsDetailsResultLiveData.getValue(), location));

        restaurantsWrapperViewStateMutableLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults ->
                combine(nearbySearchResults, restaurantsDetailsResultLiveData.getValue(), locationLiveData.getValue()));

        restaurantsWrapperViewStateMutableLiveData.addSource(restaurantsDetailsResultLiveData, placeDetailsResults ->
                combine(nearbySearchResultsLiveData.getValue(), placeDetailsResults, locationLiveData.getValue()));
    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable List<RestaurantDetailsResult> restaurantDetailsResults,
                         @Nullable Location location) {

        if(restaurantDetailsResults == null && nearbySearchResults != null){
              restaurantsWrapperViewStateMutableLiveData.setValue(map(location, nearbySearchResults));

        }else if(restaurantDetailsResults != null && nearbySearchResults != null){
            restaurantsWrapperViewStateMutableLiveData.setValue(mapWithDetails(location, nearbySearchResults, restaurantDetailsResults));

        }
    }

    // CREATE A LIST OF RESTAURANTS ITEMS WITH NEARBY RESULTS //

    // 1.NEARBY SEARCH DATA (IF DETAILS ARE NOT SUPPORTED ANYMORE)
    private RestaurantsWrapperViewState map(
            Location location,
            NearbySearchResults nearbySearchResults){

        List<RestaurantsViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++){
            String restaurantName = nearbySearchResults.getResults().get(i).getRestaurantName();
            String addressRestaurant = nearbySearchResults.getResults().get(i).getRestaurantAddress();
            String photoRestaurant = photoReference(nearbySearchResults.getResults().get(i).getRestaurantPhotos());
            String distanceRestaurant = distance(
                    location,
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            String openingHoursRestaurant = OpeningHoursWithoutDetails(nearbySearchResults.getResults().get(i).getOpeningHours());
            double ratingRestaurant = convertRatingStars(nearbySearchResults.getResults().get(i).getRating());
            String placeIdRestaurant = nearbySearchResults.getResults().get(i).getPlaceId();

            restaurantList.add(new RestaurantsViewState(
                    restaurantName,
                    addressRestaurant,
                    photoRestaurant,
                    distanceRestaurant,
                    openingHoursRestaurant,
                    ratingRestaurant,
                    placeIdRestaurant));

        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    // 2.PLACE DETAILS DATA
    private RestaurantsWrapperViewState mapWithDetails(
            Location location,
            NearbySearchResults nearbySearchResults,
            List<RestaurantDetailsResult> restaurantDetailsResults) {

        List<RestaurantsViewState> restaurantList = new ArrayList<>();

        if (restaurantDetailsResults != null) {
            for (RestaurantSearch place : nearbySearchResults.getResults()) {
                for (int i = 0; i < restaurantDetailsResults.size(); i++) {
                    if (restaurantDetailsResults.get(i).getDetailsResult().getPlaceId().equals(place.getPlaceId())) {

                        String restaurantName = place.getRestaurantName();
                        String addressRestaurant = place.getRestaurantAddress();
                        String photoRestaurant = photoReference(place.getRestaurantPhotos());
                        String distanceRestaurant = distance(
                                location,
                                place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                                place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
                        String openingHoursRestaurant = getOpeningText(restaurantDetailsResults.get(i).getDetailsResult().getOpeningHours(), place.isPermanentlyClosed());
                        double ratingRestaurant = convertRatingStars(place.getRating());
                        String placeIdRestaurant = place.getPlaceId();

                        restaurantList.add(new RestaurantsViewState(
                                restaurantName,
                                addressRestaurant,
                                photoRestaurant,
                                distanceRestaurant,
                                openingHoursRestaurant,
                                ratingRestaurant,
                                placeIdRestaurant));
                        break;

                    }
                }
            }
        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    // SEARCH FOR A PHOTO IN THE LIST PROVIDED BY NEARBY PLACES
    private String photoReference(List<Photo> photoList){
        String result = null;
        if (photoList != null){
            for (Photo photo : photoList){
                if (!photo.getPhotoReference().isEmpty()){
                    result = photo.getPhotoReference();

                }
            }
        }
        if(photoList == null){
            result =  application.getString(R.string.photo_unavailable);
        }
        return result;

    }

    // GET MESSAGE FOR THE OPENING HOUR FIELD (WITHOUT DETAILS INFORMATION)
    private String OpeningHoursWithoutDetails(OpeningHours openingHours){
        String openStatus;
        if (openingHours != null) {
            if (openingHours.getOpenNow()) {
                openStatus = application.getString(R.string.open);

            } else {
                openStatus = application.getString(R.string.closed);

            }
        } else {
            openStatus = application.getString(R.string.opening_hours_unavailable);

        }
        return openStatus;

    }

    // GET MESSAGE FOR THE OPENING HOUR FIELD
    private String getOpeningText(OpeningHours openingHours, boolean permanentlyClosedStatus) {

        // DEFAULT MESSAGE IF NO OPENING HOUR DATA
        String messageToDisplay = application.getString(R.string.opening_hours_unavailable);

        if (openingHours != null) {
            LocalDateTime currentLocalDate = LocalDateTime.now(clock);
            // IF THE PERIOD LIST HAS ONLY ONE ELEMENT, CONSIDER THAT THE PLACE IS A 24/7 ONE
            if (openingHours.getPeriods() != null) {
                if (openingHours.getPeriods().size() == 1) {
                    if (openingHours.getOpenNow()) {
                        messageToDisplay = application.getString(R.string.open_h24);

                    }
                } else if (openingHours.getPeriods().size() > 1) {
                    LocalDateTime selectedOpeningDateTime = null;
                    LocalDateTime selectedClosingDateTime = null;

                    // SET OPENING AND CLOSED HOURS IF AFTER CURRENT DATE, TAKE THE CLOSEST MATCH

                    for (Periods period : openingHours.getPeriods()) {

                        LocalDateTime openingHourToConsider = convertOpeningHours(
                                period.getOpen().getTime(),
                                period.getOpen().getDay());
                        LocalDateTime closingHourToConsider = convertOpeningHours(
                                period.getClose().getTime(),
                                period.getClose().getDay());

                        if (openingHourToConsider.isAfter(currentLocalDate)
                                && isConsiderClosestThanSelected(selectedOpeningDateTime, openingHourToConsider)) {
                            selectedOpeningDateTime = openingHourToConsider;

                        }
                        if (closingHourToConsider.isAfter(currentLocalDate)
                                && isConsiderClosestThanSelected(selectedClosingDateTime, closingHourToConsider)) {
                            selectedClosingDateTime = closingHourToConsider;

                        }
                    }
                    if (selectedOpeningDateTime != null) {
                        if (selectedOpeningDateTime.isAfter(selectedClosingDateTime)) {
                            assert selectedClosingDateTime != null;
                            LocalDateTime closingSoonDate = selectedClosingDateTime.minus(1, ChronoUnit.HOURS);
                            messageToDisplay = application.getString(R.string.open_until) + getReadableHour(selectedClosingDateTime);

                            if (currentLocalDate.isAfter(closingSoonDate)) {
                                messageToDisplay = application.getString(R.string.closing_soon);

                            }
                        } else if (selectedOpeningDateTime.isAfter(currentLocalDate)) {
                            messageToDisplay = application.getString(R.string.closed_until) +
                                    getReadableDay(selectedOpeningDateTime) +
                                    getReadableHour(selectedOpeningDateTime);

                        }
                    }
                }// IF THE PERIOD LIST IS EMPTY, RETRIEVE ONLY OPEN STATUS
            } else if (!openingHours.getOpenNow()) {
                messageToDisplay = application.getString(R.string.closed);
            } else if (openingHours.getOpenNow()) {
                messageToDisplay = application.getString(R.string.open);

            }
        }
        if (permanentlyClosedStatus) {
            messageToDisplay = application.getString(R.string.permanently_closed);

        }
        return messageToDisplay;

    }

    // GET LOCATION DISTANCE FROM USER TO RESTAURANT
    @NonNull
    private String distance(Location userLocation, double lat, double lng){
        if (userLocation != null){
            float[] results = new float[1];
            Location.distanceBetween(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    lat,
                    lng,
                    results );
            return String.valueOf((int)results[0]);

        }else {
            return application.getString(R.string.distance_unavailable);

        }
    }

    // CONVERT RATING STARS (5 -> 3)
    private double convertRatingStars(double rating) {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return Math.round(rating * 3 / 5);

    }

    /////// DATE AND TIME ///////

    private boolean isConsiderClosestThanSelected(LocalDateTime selectedHour, LocalDateTime hourToConsider) {
        if (selectedHour == null) {
            return true;
        } else return hourToConsider.isBefore(selectedHour);
    }

    private int getCurrentNumericDay(){
        LocalDateTime currentDate = LocalDateTime.now(clock);
        int dayOfWeek = currentDate.getDayOfWeek().getValue();
        // CONVERT SUNDAY TO 0 (NEEDED TO GET SAME DAY AS OPENING HOURS)
        if(dayOfWeek == 7){
            dayOfWeek = 0;
        }

        return dayOfWeek; }

    private int currentYear(){
        LocalDate currentDate = LocalDate.now(clock);
        return currentDate.getYear();

    }

    private int currentMonth(){
        LocalDate currentDate = LocalDate.now(clock);
        return currentDate.getMonthValue();

    }

    private int getCurrentDayOfMonth(){
        LocalDate currentDate = LocalDate.now(clock);
        return currentDate.getDayOfMonth();

    }

    // RETURN TRUE IF 30 DAYS MONTH
    private boolean isEvenMonth() {
        return currentMonth() == 2 ||
                currentMonth() == 4 ||
                currentMonth() == 6 ||
                currentMonth() == 9 ||
                currentMonth() == 11;
    }

    //////// CONVERTERS FOR TIME AND DATE ////////

    // CONVERT CLOSE HOUR TO READABLE UK HOUR
    private String getReadableHour(LocalDateTime selectedHour){
        String minReadable;
        String meridian;
        int hour = selectedHour.getHour();
        int minutes = selectedHour.getMinute();

        if(hour > 12){
            hour = hour - 12;
            meridian = application.getString(R.string.pm);
        }else{
            meridian = application.getString(R.string.am);
        }
        if(minutes == 0){
            minReadable = application.getString(R.string.dot);
        }else if(minutes < 10){
            minReadable = application.getString(R.string.two_dots_for_minutes) + minutes;
        }else {
            minReadable = application.getString(R.string.two_dots) + minutes;
        }
        return " " + hour + minReadable + meridian;

    }

    // IF THE NEXT OPENING IS NOT TODAY
    private String getReadableDay(LocalDateTime selectedOpeningDateTime) {
        if(selectedOpeningDateTime.getDayOfWeek() != LocalDateTime.now(clock).getDayOfWeek()){
            String str;
            if(selectedOpeningDateTime.getDayOfWeek().getValue() == LocalDateTime.now(clock).getDayOfWeek().getValue() + 1){
                str = application.getString(R.string.tomorrow);

            }else{
                 str = selectedOpeningDateTime.getDayOfWeek().toString().toLowerCase(Locale.ROOT);
            }


            return " " + str.substring(0,1).toUpperCase() + str.substring(1);

        }else{
            return "";
        }
    }

    private LocalDateTime convertOpeningHours(String time, int day) {
        String hour = time.substring(0, 2);
        String minutes = time.substring(2, 4);
        int hourInt = Integer.parseInt(hour);
        int minuteInt = Integer.parseInt(minutes);
        int dayToAdd = day - getCurrentNumericDay();

        if (dayToAdd < 0) {
            dayToAdd = dayToAdd + 7;
        }

        int dayOfMonth = getCurrentDayOfMonth() + dayToAdd;
        int month = currentMonth();
        int year = currentYear();

        if (dayOfMonth > 30 && isEvenMonth()) {
            dayOfMonth = dayOfMonth - 30;
            month = month + 1;

        }
        if (dayOfMonth > 31) {
            dayOfMonth = dayOfMonth - 31;
            month = month + 1;

        }
        if (month > 12) {
            month = 1;
            year = year + 1;

        }
        return LocalDateTime.of(year, month, dayOfMonth, hourInt, minuteInt);

    }

    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<RestaurantsWrapperViewState> getRestaurantsViewStateLiveData() {
        return restaurantsWrapperViewStateMutableLiveData;

    }
}