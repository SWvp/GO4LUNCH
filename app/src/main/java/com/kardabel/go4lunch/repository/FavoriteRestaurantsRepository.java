package com.kardabel.go4lunch.repository;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.kardabel.go4lunch.pojo.FavoriteRestaurant;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRestaurantsRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = GetCurrentUserIdUseCase.getCurrentUserUID();


    // GET THE FAVORITE RESTAURANTS FOR ALL USERS
    public LiveData<List<FavoriteRestaurant>> getFavoriteRestaurants() {
        MutableLiveData<List<FavoriteRestaurant>> favoriteRestaurantsLiveData = new MutableLiveData<>();

        db.collection("users")
                .document(userId)
                .collection("favorite restaurants")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("no favorite today", error.getMessage());
                            return;
                        }
                        List<FavoriteRestaurant> favoriteRestaurants = new ArrayList<>();

                        for (DocumentChange document : value.getDocumentChanges()) {
                            if (document.getType() == DocumentChange.Type.ADDED ||
                                    document.getType() == DocumentChange.Type.MODIFIED) {

                                favoriteRestaurants.add(document.getDocument().toObject(FavoriteRestaurant.class));

                            }
                        }
                        favoriteRestaurantsLiveData.setValue(favoriteRestaurants);

                    }
                });
        return favoriteRestaurantsLiveData;

    }
}
