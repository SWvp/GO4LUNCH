package com.kardabel.go4lunch.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClickOnFavoriteRestaurantUseCase {

    private final FirebaseFirestore firebaseFirestore;

    public static final String COLLECTION_USERS = "users";
    public static final String FAVORITE_RESTAURANTS = "favorite restaurants";
    public static final String RESTAURANT_NAME = "restaurantName";
    public static final String RESTAURANT_ID = "restaurantId";

    public ClickOnFavoriteRestaurantUseCase(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;

    }

    public  CollectionReference getDayCollection() {
        return firebaseFirestore.collection(COLLECTION_USERS);
    }

    // WHEN FAVORITE ICON FROM DETAIL VIEW IS CLICKED,
    // ADD OR REMOVE THE RESTAURANT FROM THE LIST (DOCUMENT IN FIRESTORE) OF USER'S FAVORITE
    public void onFavoriteRestaurantClick(
            String restaurantId,
            String restaurantName) {

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Map<String, Object> favoriteRestaurant = new HashMap<>();
        favoriteRestaurant.put(RESTAURANT_ID, restaurantId);
        favoriteRestaurant.put(RESTAURANT_NAME, restaurantName);

        getDayCollection()
                .document(userId)
                .collection(FAVORITE_RESTAURANTS)
                .document(restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            task.getResult().getReference().delete();
                        } else {
                            task.getResult().getReference().set(favoriteRestaurant);
                        }
                    }
                });
    }
}
