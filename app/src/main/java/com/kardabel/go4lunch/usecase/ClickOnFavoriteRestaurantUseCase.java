package com.kardabel.go4lunch.usecase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClickOnFavoriteRestaurantUseCase {

    public static CollectionReference getDayCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // WHEN FAVORITE ICON FROM DETAIL VIEW IS CLICKED,
    // ADD OR REMOVE THE RESTAURANT FROM THE LIST (DOCUMENT IN FIRESTORE) OF USER'S FAVORITE
    public static void onFavoriteRestaurantClick(
            String restaurantId,
            String restaurantName) {

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Map<String, Object> favoriteRestaurant = new HashMap<>();
        favoriteRestaurant.put("restaurantId", restaurantId);
        favoriteRestaurant.put("restaurantName", restaurantName);

        assert userId != null;
        getDayCollection()
                .document(userId)
                .collection("favorite restaurants")
                .document(restaurantId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                task.getResult().getReference().delete();
                            } else {
                                task.getResult().getReference().set(favoriteRestaurant);
                            }
                        }
                    }
                });
    }
}
