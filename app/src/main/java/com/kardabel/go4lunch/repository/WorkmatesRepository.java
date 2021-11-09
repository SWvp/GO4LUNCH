package com.kardabel.go4lunch.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkmatesRepository {

    public LiveData<List<UserModel>> getWorkmates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MutableLiveData<List<UserModel>> userModelMutableLiveData = new MutableLiveData<>();
        List<UserModel> workmates = new ArrayList<>();

        // TODO : order by "is restaurant been chosen"
        db.collection("users")
               // .orderBy("userName", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        for(DocumentChange document : value.getDocumentChanges()){
                            if(document.getType() == DocumentChange.Type.ADDED || document.getType() == DocumentChange.Type.MODIFIED){
                                workmates.add(document.getDocument().toObject(UserModel.class));
                            }
                        }
                        userModelMutableLiveData.setValue(workmates);

                    }
                });
        return userModelMutableLiveData;
    }

    public LiveData<List<RestaurantSearch>> getRestaurantsWithFavorite() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MutableLiveData<List<RestaurantSearch>> userModelMutableLiveData = new MutableLiveData<>();
        List<RestaurantSearch> restaurants = new ArrayList<>();

        LocalDate today = LocalDate.now();

        db.collection("days")
                .document(today.toString())
                .collection("favorite restaurants")
                //.whereEqualTo(today.toString(), true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){
                            Log.e("no favorite today", error.getMessage());
                            return;
                        }
                        for(DocumentChange document : value.getDocumentChanges()){
                            if(document.getType() == DocumentChange.Type.ADDED || document.getType() == DocumentChange.Type.MODIFIED){
                                restaurants.add(document.getDocument().toObject(RestaurantSearch.class));
                //             restaurants.add(document.getDocument().toObject(RestaurantSearch.class));
                //             db
                //                     .collection("days")
                //                     .document(today.toString())
                //                     .collection("favorite restaurants")
                //                     .document();
                            }
                        }
                        userModelMutableLiveData.setValue(restaurants);

                    }
                });
        return userModelMutableLiveData;
    }
}
