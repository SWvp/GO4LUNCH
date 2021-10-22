package com.kardabel.go4lunch.ui.listview;// TODO STEPHANE : attention au nom de package

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.pojo.Close;
import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Periods;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.util.CurrentNumericDay;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RestaurantsViewModel extends ViewModel {

    // TODO STEPHANE : Choisis ! Soit un / des delegates (CurrentNumericDay), soit la clock, mais pas les 2 :D
    private final CurrentNumericDay currentNumericDay;


    private final MediatorLiveData<RestaurantsWrapperViewState> restaurantsWrapperViewStateMutableLiveData = new MediatorLiveData<>();

    private final Application application;
    private final Clock clock;

    public RestaurantsViewModel(
                                @NonNull Application application,
                                @NonNull LocationRepository locationRepository,
                                @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase,
                                @NonNull RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase,
                                @NonNull CurrentNumericDay currentNumericDay,
                                @NonNull Clock clock
    ){


        this.clock = clock;
        this.application = application;
        this.currentNumericDay = currentNumericDay;


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
            String openingHoursRestaurant = OpeningHoursLight(nearbySearchResults.getResults().get(i).getOpeningHours());
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
        if (photoList != null){
            for (Photo photo : photoList){
                if (!photo.getPhotoReference().isEmpty()){
                    return photo.getPhotoReference();

                }
            }
        }
        return application.getString(R.string.photo_unavailable);

    }

    // GET MESSAGE FOR THE OPENING HOUR FIELD (WITHOUT DETAILS INFORMATION)
    // TODO STEPHANE : function naming
    private String OpeningHoursLight(OpeningHours openingHours){
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
            if (openingHours.getPeriods().size() == 1) {
                if (openingHours.getOpenNow()) {
                    messageToDisplay = application.getString(R.string.open_h24);

                }

            } else if (openingHours.getPeriods().size() > 1) {
                if (isRestaurantOpenToday(openingHours)) {

                    LocalDateTime selectedOpeningDateTime = null;
                    LocalDateTime selectedClosingDateTime = null;

                    // SET OPENING AND CLOSED HOURS IF AFTER CURRENT DATE, TAKE THE CLOSEST MATCH

                    for (Periods period : openingHours.getPeriods()) {

                        LocalDateTime openingHourToConsider = convertOpeningHourToLocalDateTime(period.getOpen().getTime(), period.getOpen().getDay());
                        LocalDateTime closingHourToConsider = convertClosingHourToLocalDateTime(period.getClose());

                        if(openingHourToConsider.isAfter(currentLocalDate) && isConsiderClosestThanSelected(selectedOpeningDateTime, openingHourToConsider)){
                            selectedOpeningDateTime = openingHourToConsider;

                        }else if(closingHourToConsider.isAfter(currentLocalDate) && isConsiderClosestThanSelected(selectedClosingDateTime, closingHourToConsider)){
                            selectedClosingDateTime = closingHourToConsider;

                        }
                    }

                    if (selectedOpeningDateTime != null) {
                        if (selectedOpeningDateTime.isAfter(selectedClosingDateTime)) {

                            assert selectedClosingDateTime != null;
                            LocalDateTime closingSoonDate = selectedClosingDateTime.minus(1, ChronoUnit.HOURS);

                            messageToDisplay = application.getString(R.string.open_until) +
                                    getReadableHour(
                                        // TODO STEPHANE : mets ça aussi dans ta méthode getReadableHour (ne donne que "selectedClosingDateTime")
                                        Integer.parseInt(selectedClosingDateTime.format(DateTimeFormatter.ofPattern("HHmm")))
                                    );

                            if (currentLocalDate.isAfter(closingSoonDate)) {
                                messageToDisplay = application.getString(R.string.closing_soon);

                            }

                        } else if (selectedOpeningDateTime.isAfter(currentLocalDate)) {
                            messageToDisplay = application.getString(R.string.closed_until) +
                                    getReadableDay(selectedOpeningDateTime) +
                                    getReadableHour(Integer.parseInt(selectedOpeningDateTime.format(DateTimeFormatter.ofPattern("HHmm"))));
                        }
                    }

                }else{
                    messageToDisplay = application.getString(R.string.closed_today);

                }
            }

            // IF THE PERIOD LIST IS EMPTY OR NULL, RETRIEVE ONLY OPEN STATUS

            else if (!openingHours.getOpenNow()) {
                messageToDisplay = application.getString(R.string.closed);


            } else if (openingHours.getOpenNow()) {
                messageToDisplay = application.getString(R.string.open);

            }
        }

        if(permanentlyClosedStatus){
            messageToDisplay = application.getString(R.string.permanently_closed);
        }
        return messageToDisplay;

    }




    // GET LOCATION DISTANCE FROM USER TO RESTAURANT
    @NonNull
    private String distance(Location userLocation, double lat, double lng){
        if (userLocation != null){
            // TODO STEPHANE : utilise plutôt distanceBetween pour éviter de créer une fake location
        Location restaurantLocation = new Location("restaurant location");
        restaurantLocation.setLatitude(lat);
        restaurantLocation.setLongitude(lng);
        float distance = userLocation.distanceTo(restaurantLocation);
        return (int) distance + application.getString(R.string.m);
        } else {
            return application.getString(R.string.distance_unavailable);

        }
    }

    // CONVERT RATING STARS (5 -> 3)
    private double convertRatingStars(double rating) {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return Math.round(rating * 3 / 5);

    }

    // CONVERT PHOTO REFERENCE TO URL
    @NonNull
    public static String urlPhoto(RestaurantsViewState restaurantsViewState) {
        String API_URL = "https://maps.googleapis.com/maps/api/place/";
        String PHOTO_REFERENCE = "photo?maxwidth=300&photo_reference=";
        String API_KEY = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8"; // TODO STEPHANE : A protéger grâce à Gradle
        if (restaurantsViewState.getAvatar() != null) {
            String photoReference = restaurantsViewState.getAvatar();
            return API_URL + PHOTO_REFERENCE + photoReference + "&key=" + API_KEY;
        }
        return "";
    }

    /////// DATE AND TIME ///////

    private boolean isConsiderClosestThanSelected(LocalDateTime selectedHour, LocalDateTime hourToConsider) {
        if (selectedHour == null) {
            return true;
        } else return hourToConsider.isBefore(selectedHour);
    }

    // TODO STEPHANE : Closed (comment) or opened (function name) ?
    // CHECK IF RESTAURANT IS CLOSED THIS CURRENT DAY
    private boolean isRestaurantOpenToday(@NonNull OpeningHours openingHours) {
        // TODO STEPHANE : Je suis sur que tu peux simplifier et éviter de boucler 2 fois :)
        boolean bool = true;
        boolean isRestaurantActivityToday = false;

        // IF THE PERIOD LIST DON'T HAVE THIS NUMERIC DAY, WE CAN SAY THERE IS NO ACTIVITY TODAY
        for (int i = 0; i < openingHours.getPeriods().size(); i++) {
            if (openingHours.getPeriods().get(i).getOpen().getDay() == getCurrentNumericDay()) {
                isRestaurantActivityToday = true;
            }
        }

        for (int i = 0; i < openingHours.getPeriods().size(); i++) {
            if ((openingHours.getPeriods().get(i).getOpen().getDay() == getCurrentNumericDay() &&
                    openingHours.getPeriods().get(i).getOpen().getTime() == null) ||
                    !isRestaurantActivityToday) {
                bool = false;

            }
        }
        return bool;

    }

    // TODO STEPHANE : Non nécessaire
    private int getCurrentNumericDay(){ return currentNumericDay.getCurrentNumericDay(); }

    // TODO STEPHANE : Arguably non nécessaire non plus (pour les 3 méthodes plus bas)
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
    private boolean isCurrentDayTheLastMonthDay() {
        return currentMonth() == 2 ||
                currentMonth() == 4 ||
                currentMonth() == 6 ||
                currentMonth() == 9 ||
                currentMonth() == 11;
    }

    private boolean isNotHappyNewYear() {
        return currentMonth() == 12;
    }

    //////// CONVERTERS FOR TIME AND DATE ////////

    // CONVERT CLOSE HOUR TO READABLE UK HOUR
    @NonNull
    private String getReadableHour(int closingHour) {
        int hour;
        int min;

        String minReadable;
        String readableHour;

        // TODO STEPHANE : 0 / 100 = 0, tu peux virer le if
        if (closingHour == 0) {
            hour = 0;
        } else {
            hour = closingHour / 100;
        }

        min = closingHour - (hour * 100);

        // CONVERT MINUTES IF NEEDED
        if (min == 0) {
            minReadable = "";
        } else if (min > 0 && min < 10) {
            minReadable = ":0" + min;
        } else {
            minReadable = ":" + min;
        }

        // GET READABLE HOUR
        if (hour < 12 && hour > 0) {
            readableHour = " " + hour + minReadable + "." +application.getString(R.string.am);
        } else if (hour == 0) {
            readableHour = " " + 12 + minReadable + "." + application.getString(R.string.am);
        } else if (hour == 12) {
            readableHour = " " + 12 + minReadable + "." + application.getString(R.string.pm);
        } else {
            readableHour = " " + (hour - 12) + minReadable + "." + application.getString(R.string.pm);
        }
        return readableHour;
    }

    // IF THE NEXT OPENING IS NOT TODAY
    private String getReadableDay(LocalDateTime selectedOpeningDateTime) {
        if(selectedOpeningDateTime.getDayOfWeek() != LocalDateTime.now(clock).getDayOfWeek()){
            return " " + selectedOpeningDateTime.getDayOfWeek().toString();

        }else{
            return "";
        }
    }

    private LocalDateTime convertOpeningHourToLocalDateTime(String openingHour, int openingDay) {
        int dayToAdd = openingDay - getCurrentNumericDay();

        if (dayToAdd < 0) {
            dayToAdd = dayToAdd + 7;
        }
        int openingDayOfMonth = getCurrentDayOfMonth() + dayToAdd;
        String openingDayReadable = String.valueOf(openingDayOfMonth);

        if (openingDayOfMonth < 10) {
            openingDayReadable = "0" + getCurrentDayOfMonth();


        }
        String convertedOpeningHour = currentYear() + "-" + currentMonth() + "-" + openingDayReadable + " " + convertToReadableHour(openingHour);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return LocalDateTime.parse(convertedOpeningHour, formatter);

    }

    private LocalDateTime convertClosingHourToLocalDateTime(@NonNull Close closingHour) {
        // GET A DEFAULT DATE VALUE FOR PARSING
        // TODO STEPHANE : Jamais de valeur magique !
        String convertedClosingDate = "2000-01-01 00:00";
        int dayToAdd = closingHour.getDay() - getCurrentNumericDay();

        if (dayToAdd < 0) {
            dayToAdd = dayToAdd + 7;
        }
        int closingDayOfMonth = getCurrentDayOfMonth() + dayToAdd;
        String closingDayReadable = String.valueOf(closingDayOfMonth);

        if (closingDayOfMonth < 10) {
            closingDayReadable = "0" + getCurrentDayOfMonth();

        }

        if (closingDayOfMonth < 30) {

            if (closingHour.getDay() == getCurrentNumericDay()) {
                // TODO STEPHANE : Je pense que tu peux encapsuler ça dans une fonction
                convertedClosingDate = currentYear() + "-" + currentMonth() + "-" + closingDayReadable + " " + convertToReadableHour(closingHour.getTime());

            } else if (closingHour.getDay() == (getCurrentNumericDay() + 1)) {
                int tomorrow = getCurrentDayOfMonth() + 1;
                closingDayReadable = String.valueOf(tomorrow);
                if (tomorrow < 10) {
                    closingDayReadable = "0" + tomorrow;

                }
                convertedClosingDate = currentYear() + "-" + currentMonth() + "-" + closingDayReadable + " " + convertToReadableHour(closingHour.getTime());

            }

        } else if (closingDayOfMonth < 31) {
            // TODO STEPHANE : - et - ça fait + ! :D
            if (!isNotHappyNewYear()) {
                if (isCurrentDayTheLastMonthDay()) {
                    int currentMonth = currentMonth() + 1;
                    convertedClosingDate = currentYear() + "-" + currentMonth + "-0" + 1 + " " + convertToReadableHour(closingHour.getTime());

                } else {
                    int currentIntDay = closingDayOfMonth + 1;
                    convertedClosingDate = currentYear() + "-" + currentMonth() + "-" + currentIntDay + " " + convertToReadableHour(closingHour.getTime());

                }
            } else {
                int currentYear = currentYear() + 1;
                convertedClosingDate = currentYear + "-" + "01" + "01" + " " + convertToReadableHour(closingHour.getTime());

            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // TODO STEPHANE : Commentaire global sur toute cette fonction :
        //  plutôt que de construire une string pour ensuite la parser, pourquoi ne pas construire direct la LocalDateTime ?
        return LocalDateTime.parse(convertedClosingDate, formatter);

    }

    // CONVERT OPENING OR CLOSING HOUR TO READABLE HOUR WITH ":"
    public String convertToReadableHour(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(2, ":");
        return sb.toString();
    }

    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<RestaurantsWrapperViewState> getRestaurantsViewStateLiveData() {
        return restaurantsWrapperViewStateMutableLiveData;

    }
}