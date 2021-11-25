package com.kardabel.go4lunch.usecase;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ChangeFavoriteStateUseCase {

    public static CollectionReference getDayCollection() {
        return FirebaseFirestore.getInstance().collection(LocalDate.now().toString());
    }

    // CHECK IF USER HAVE ALREADY SET THE RESTAURANT AS FAVORITE,
    // IF YES, JUST DELETE,
    // IF NO, DELETE OLD ONE AND CREATE A NEW FAVORITE RESTAURANT

    public static void onFavoriteClick(String placeId, String restaurantName) {

        String userId = GetCurrentUserIdUseCase.getCurrentUserUID();

        Map<String, Object> userGotRestaurant = new HashMap<>();
        userGotRestaurant.put("restaurantId", placeId);
        userGotRestaurant.put("restaurantName", restaurantName);
        userGotRestaurant.put("userId", userId);

        getDayCollection().document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        task.getResult().getReference().delete();
                    } else {
                        task.getResult().getReference().set(userGotRestaurant);
                    }
                }
            }
        });
    }
}
