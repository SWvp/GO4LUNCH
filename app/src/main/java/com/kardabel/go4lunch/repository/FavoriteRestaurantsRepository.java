package com.kardabel.go4lunch.repository;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.kardabel.go4lunch.pojo.FavoriteRestaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoriteRestaurantsRepository {

    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_FAVORITE_RESTAURANTS = "favorite restaurants";

    // GET THE FAVORITE RESTAURANTS FOR CURRENT USER
    public LiveData<List<FavoriteRestaurant>> getFavoriteRestaurants() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        MutableLiveData<List<FavoriteRestaurant>> favoriteRestaurantsLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_FAVORITE_RESTAURANTS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("no favorite", error.getMessage());
                            return;
                        }
                        List<FavoriteRestaurant> favoriteRestaurants = new ArrayList<>();

                        for (DocumentChange document : value.getDocumentChanges()) {
                            if (document.getType() == DocumentChange.Type.ADDED) {

                                favoriteRestaurants.add(document.getDocument().toObject(FavoriteRestaurant.class));

                            } else if (document.getType() == DocumentChange.Type.REMOVED) {

                                favoriteRestaurants.remove(document.getDocument().toObject(FavoriteRestaurant.class));

                            }
                        }
                        favoriteRestaurantsLiveData.setValue(favoriteRestaurants);

                    }
                });
        return favoriteRestaurantsLiveData;

    }
}
