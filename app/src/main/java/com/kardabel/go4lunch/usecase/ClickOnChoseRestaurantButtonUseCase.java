package com.kardabel.go4lunch.usecase;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClickOnChoseRestaurantButtonUseCase {

    private final Clock clock;

    public ClickOnChoseRestaurantButtonUseCase(Clock clock) {
        this.clock = clock;

    }

    public CollectionReference getDayCollection() {
        return FirebaseFirestore.getInstance().collection(LocalDate.now(clock).toString());
    }

    // CHECK IF USER HAVE ALREADY SET THE CHOSEN RESTAURANT,
    // IF YES, JUST DELETE,
    // IF NO, DELETE OLD ONE AND CREATE A NEW CHOSEN RESTAURANT
    public void onRestaurantSelectedClick(
            String restaurantId,
            String restaurantName,
            String restaurantAddress) {

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Map<String, Object> userGotRestaurant = new HashMap<>();
        userGotRestaurant.put("restaurantId", restaurantId);
        userGotRestaurant.put("restaurantName", restaurantName);
        userGotRestaurant.put("userId", userId);
        userGotRestaurant.put("restaurantAddress", restaurantAddress);

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
