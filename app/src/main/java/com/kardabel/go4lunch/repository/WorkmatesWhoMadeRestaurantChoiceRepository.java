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
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.model.WorkmateWhoMadeRestaurantChoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkmatesWhoMadeRestaurantChoiceRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // GET WORKMATES WHO DECIDED WHERE THEY WOULD EAT
    public LiveData<List<WorkmateWhoMadeRestaurantChoice>> getWorkmatesWhoMadeRestaurantChoice() {
        MutableLiveData<List<WorkmateWhoMadeRestaurantChoice>> userModelMutableLiveData = new MutableLiveData<>();

        LocalDate today = LocalDate.now();

        List<WorkmateWhoMadeRestaurantChoice> usersWithRestaurant = new ArrayList<>();

        db.collection(today.toString())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("restaurant choice error", error.getMessage());
                            return;
                        }
                        //  List<WorkmateWhoMadeRestaurantChoice> userWithRestaurant = new ArrayList<>();

                        assert value != null;
                        for (DocumentChange document : value.getDocumentChanges()) {
                            Log.d("pipo", "onEvent() called with: value = [" + document.getDocument().toObject(WorkmateWhoMadeRestaurantChoice.class) + "], error = [" + error + "]");
                            if (document.getType() == DocumentChange.Type.ADDED ) {

                                usersWithRestaurant.add(document.getDocument().toObject(WorkmateWhoMadeRestaurantChoice.class));

                            }

                            else if (document.getType() == DocumentChange.Type.MODIFIED ) {

                                for (int i = 0; i < usersWithRestaurant.size(); i++) {
                                    if(usersWithRestaurant.get(i).getUserId().equals(document.getDocument().toObject(WorkmateWhoMadeRestaurantChoice.class).getUserId())){
                                        usersWithRestaurant.remove(usersWithRestaurant.get(i));
                                    }

                                }

                                usersWithRestaurant.add(document.getDocument().toObject(WorkmateWhoMadeRestaurantChoice.class));

                            }

                            else if(document.getType() == DocumentChange.Type.REMOVED){


                                usersWithRestaurant.remove(document.getDocument().toObject(WorkmateWhoMadeRestaurantChoice.class));

                            }

                           // TODO: SWITCH CASE
                        }

                       // List<WorkmateWhoMadeRestaurantChoice> usersWhoChose = new ArrayList<>(usersWithRestaurant);
                        userModelMutableLiveData.setValue(usersWithRestaurant);
                    }
                });
        return userModelMutableLiveData;

    }
}
