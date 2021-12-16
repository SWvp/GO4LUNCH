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

    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseAuth firebaseAuth;
    private final Clock clock;

    public ClickOnChoseRestaurantButtonUseCase(FirebaseFirestore firebaseFirestore,
                                               FirebaseAuth firebaseAuth,
                                               Clock clock) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        this.clock = clock;

    }

    public CollectionReference getDayCollection() {
        return firebaseFirestore.collection(LocalDate.now(clock).toString());
    }

    // CHECK IF USER HAVE ALREADY SET THE CHOSEN RESTAURANT,
    // IF YES, JUST DELETE,
    // IF NO, DELETE OLD ONE AND CREATE A NEW CHOSEN RESTAURANT
    public void onRestaurantSelectedClick(
            String restaurantId,
            String restaurantName,
            String restaurantAddress) {

        if (firebaseAuth.getCurrentUser() != null) {

            String userId = firebaseAuth.getCurrentUser().getUid();
            String userName = firebaseAuth.getCurrentUser().getDisplayName();

            Map<String, Object> userGotRestaurant = new HashMap<>();
            userGotRestaurant.put("restaurantId", restaurantId);
            userGotRestaurant.put("restaurantName", restaurantName);
            userGotRestaurant.put("userId", userId);
            userGotRestaurant.put("userName", userName);
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
}
