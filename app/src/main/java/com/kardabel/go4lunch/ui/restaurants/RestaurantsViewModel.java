package com.kardabel.go4lunch.ui.restaurants;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.UserWithFavoriteRestaurant;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Periods;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.util.OpeningHoursColorViewAction;
import com.kardabel.go4lunch.util.SingleLiveEvent;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantsViewModel extends ViewModel {

    private final MediatorLiveData<RestaurantsWrapperViewState> restaurantsWrapperViewStateMediatorLiveData = new MediatorLiveData<>();

    private SingleLiveEvent<OpeningHoursColorViewAction> actionSingleLiveEvent = new SingleLiveEvent<>();

    private final Application application;
    private final Clock clock;

    //private static final String googleMapApiKey = BuildConfig.
    public RestaurantsViewModel(
                                @NonNull Application application,
                                @NonNull LocationRepository locationRepository,
                                @NonNull GetNearbySearchResultsUseCase getNearbySearchResultsUseCase,
                                @NonNull GetRestaurantDetailsResultsUseCase getRestaurantDetailsResultsUseCase,
                                @NonNull WorkmatesRepository workmatesRepository,
                                @NonNull Clock clock
    ){

        this.clock = clock;
        this.application = application;

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<List<RestaurantDetailsResult>> restaurantsDetailsResultLiveData = getRestaurantDetailsResultsUseCase.getPlaceDetailsResultLiveData();
        LiveData<NearbySearchResults> nearbySearchResultsLiveData = getNearbySearchResultsUseCase.getNearbySearchResultsLiveData();
        LiveData<List<UserWithFavoriteRestaurant>> favoriteRestaurantLiveData = workmatesRepository.getRestaurantsAddedAsFavorite();

        // OBSERVERS

        restaurantsWrapperViewStateMediatorLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults ->
                combine(
                        nearbySearchResults,
                        restaurantsDetailsResultLiveData.getValue(),
                        locationLiveData.getValue(),
                        favoriteRestaurantLiveData.getValue()));

        restaurantsWrapperViewStateMediatorLiveData.addSource(restaurantsDetailsResultLiveData, restaurantDetailsResults ->
                combine(
                        nearbySearchResultsLiveData.getValue(),
                        restaurantDetailsResults,
                        locationLiveData.getValue(),
                        favoriteRestaurantLiveData.getValue()));

        restaurantsWrapperViewStateMediatorLiveData.addSource(locationLiveData, location ->
                combine(
                        nearbySearchResultsLiveData.getValue(),
                        restaurantsDetailsResultLiveData.getValue(),
                        location,
                        favoriteRestaurantLiveData.getValue()));

        restaurantsWrapperViewStateMediatorLiveData.addSource(favoriteRestaurantLiveData, userWithFavoriteRestaurants -> combine(
                nearbySearchResultsLiveData.getValue(),
                restaurantsDetailsResultLiveData.getValue(),
                locationLiveData.getValue(),
                userWithFavoriteRestaurants

        ));
    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable List<RestaurantDetailsResult> restaurantDetailsResults,
                         @Nullable Location location,
                         @Nullable List<UserWithFavoriteRestaurant> favoriteRestaurants) {

        if(restaurantDetailsResults == null && nearbySearchResults != null){
              restaurantsWrapperViewStateMediatorLiveData.setValue(map(location, nearbySearchResults, favoriteRestaurants));

        }else if(restaurantDetailsResults != null && nearbySearchResults != null){
            restaurantsWrapperViewStateMediatorLiveData.setValue(mapWithDetails(location, nearbySearchResults, restaurantDetailsResults, favoriteRestaurants));

        }
    }

    // 1.NEARBY SEARCH DATA (IF DETAILS ARE NOT SUPPORTED ANYMORE CAUSE TO CONNECTION PROBLEM)
    private RestaurantsWrapperViewState map(
            Location location,
            NearbySearchResults nearbySearchResults,
            List<UserWithFavoriteRestaurant> favoriteRestaurants){

        List<RestaurantsViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++){
            String name = nearbySearchResults.getResults().get(i).getRestaurantName();
            String address = nearbySearchResults.getResults().get(i).getRestaurantAddress();
            String photo = photoReference(nearbySearchResults.getResults().get(i).getRestaurantPhotos());
            String distance = distance(
                    location,
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            String openingHours = OpeningHoursWithoutDetails(nearbySearchResults.getResults().get(i).getOpeningHours());
            double rating = convertRatingStars(nearbySearchResults.getResults().get(i).getRating());
            String restaurantId = nearbySearchResults.getResults().get(i).getRestaurantId();
            String like = like(restaurantId,favoriteRestaurants);

            restaurantList.add(new RestaurantsViewState(
                    name,
                    address,
                    photo,
                    distance,
                    openingHours,
                    rating,
                    restaurantId,
                    like));

        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    // 2.NEARBY SEARCH WITH PLACE DETAILS DATA
    private RestaurantsWrapperViewState mapWithDetails(
            Location location,
            NearbySearchResults nearbySearchResults,
            List<RestaurantDetailsResult> restaurantDetailsResults,
            List<UserWithFavoriteRestaurant> favoriteRestaurants) {

        List<RestaurantsViewState> restaurantList = new ArrayList<>();

        if (restaurantDetailsResults != null) {
            for (RestaurantSearch place : nearbySearchResults.getResults()) {
                for (int i = 0; i < restaurantDetailsResults.size(); i++) {
                    if (restaurantDetailsResults.get(i).getDetailsResult().getPlaceId().equals(place.getRestaurantId())) {

                        String name = place.getRestaurantName();
                        String address = place.getRestaurantAddress();
                        String photo = photoReference(place.getRestaurantPhotos());
                        String distance = distance(
                                location,
                                place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                                place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
                        String openingHours = getOpeningText(restaurantDetailsResults.get(i).getDetailsResult().getOpeningHours(), place.isPermanentlyClosed());
                        double rating = convertRatingStars(place.getRating());
                        String restaurantId = place.getRestaurantId();
                        String like = like(restaurantId,favoriteRestaurants);

                        restaurantList.add(new RestaurantsViewState(
                                name,
                                address,
                                photo,
                                distance,
                                openingHours,
                                rating,
                                restaurantId,
                                like));
                        break;

                    }
                }
            }
        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    private String like(String restaurantId, List<UserWithFavoriteRestaurant> favoriteRestaurants) {
        int likes = 0;
        String likeAsString;
        if(favoriteRestaurants!= null){
            for (int i = 0; i < favoriteRestaurants.size(); i++) {
                if(favoriteRestaurants.get(i).getRestaurantId().equals(restaurantId)){
                    likes += 1;
                }
            }
        }

        if(likes != 0){
            likeAsString = "(" + likes + ")";
        }else{
            likeAsString = "";
        }

        return likeAsString;

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

    // GET MESSAGE FOR THE OPENING HOUR FIELD IN CASE THERE IS NO DETAILS INFORMATION
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

    // GET MESSAGE FOR THE OPENING HOUR FIELD WITH DETAILS
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

    //////// CONVERTERS FOR DATE AND TIME ////////

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
        return restaurantsWrapperViewStateMediatorLiveData;

    }

    public SingleLiveEvent<OpeningHoursColorViewAction> getActionSingleLiveEvent() {
        return actionSingleLiveEvent;

    }
}