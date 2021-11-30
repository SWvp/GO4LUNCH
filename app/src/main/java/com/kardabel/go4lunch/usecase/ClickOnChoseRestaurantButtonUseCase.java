package com.kardabel.go4lunch.usecase;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClickOnChoseRestaurantButtonUseCase {

    public static CollectionReference getDayCollection() {
        return FirebaseFirestore.getInstance().collection(LocalDate.now().toString());
    }

    // CHECK IF USER HAVE ALREADY SET THE CHOSEN RESTAURANT,
    // IF YES, JUST DELETE,
    // IF NO, DELETE OLD ONE AND CREATE A NEW CHOSEN RESTAURANT
    public static void onRestaurantSelectedClick(
            String restaurantId,
            String restaurantName) {

        String userId = GetCurrentUserIdUseCase.getCurrentUserUID();

        Map<String, Object> userGotRestaurant = new HashMap<>();
        userGotRestaurant.put("restaurantId", restaurantId);
        userGotRestaurant.put("restaurantName", restaurantName);
        userGotRestaurant.put("userId", userId);

        assert userId != null;
        getDayCollection()
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Objects.equals(task.getResult().get("restaurantId"), restaurantId)) {
                            task.getResult().getReference().delete();
                        } else {
                            task.getResult().getReference().set(userGotRestaurant);
                        }
                    }
                });
    }
}
