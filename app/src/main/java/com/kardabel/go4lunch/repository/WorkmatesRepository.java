package com.kardabel.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.UserWithFavoriteRestaurant;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkmatesRepository {


    // GET WORKMATES FROM FIRESTORE DATABASE
    public LiveData<List<UserModel>> getWorkmates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MutableLiveData<List<UserModel>> userModelMutableLiveData = new MutableLiveData<>();

        // WITH SET, WE ENSURE THERE IS NO DUPLICATE, FOR EXAMPLE WHEN ANOTHER USER CHANGE NAME FIELD
        Set<UserModel> workmates = new HashSet<>();

        // TODO : order by "is restaurant been chosen"
        db.collection("users")
                .orderBy("userName")
                .addSnapshotListener((value, error) -> {

                    if(error != null){
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }
                    for(DocumentChange document : value.getDocumentChanges()){
                        if(
                                document.getType() == DocumentChange.Type.ADDED ||
                                document.getType() == DocumentChange.Type.MODIFIED){

                            workmates.add(document.getDocument().toObject(UserModel.class));

                        }
                    }
                    List<UserModel> workmatesList = new ArrayList<>(workmates);
                    userModelMutableLiveData.setValue(workmatesList);

                });
        return userModelMutableLiveData;
    }


    // GET WORMATES WHO DECIDED WHERE THEY WOULD EAT
    public LiveData<List<UserWithFavoriteRestaurant>> getRestaurantsAddedAsFavorite() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MutableLiveData<List<UserWithFavoriteRestaurant>> userModelMutableLiveData = new MutableLiveData<>();
        List<UserWithFavoriteRestaurant> userWithRestaurant = new ArrayList<>();

        LocalDate today = LocalDate.now();

        db.collection(today.toString())
                .addSnapshotListener((value, error) -> {

                    if(error != null){
                        Log.e("no favorite today", error.getMessage());
                        return;
                    }
                    for(DocumentChange document : value.getDocumentChanges()){
                        if(
                                document.getType() == DocumentChange.Type.ADDED ||
                                document.getType() == DocumentChange.Type.MODIFIED){

                            userWithRestaurant.add(document.getDocument().toObject(UserWithFavoriteRestaurant.class));

                        }
                    }
                    userModelMutableLiveData.setValue(userWithRestaurant);
                });
        return userModelMutableLiveData;
    }
}
