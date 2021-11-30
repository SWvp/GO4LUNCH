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
import com.kardabel.go4lunch.model.WorkmateWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkmatesWhoMadeRestaurantChoiceRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = GetCurrentUserIdUseCase.getCurrentUserUID();

    // GET WORMATES WHO DECIDED WHERE THEY WOULD EAT
    public LiveData<List<WorkmateWhoMadeRestaurantChoice>> getWorkmatesWhoMadeRestaurantChoice() {
        MutableLiveData<List<WorkmateWhoMadeRestaurantChoice>> userModelMutableLiveData = new MutableLiveData<>();

        LocalDate today = LocalDate.now();

        db.collection(today.toString())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("restaurant choice error", error.getMessage());
                            return;
                        }
                        List<WorkmateWhoMadeRestaurantChoice> userWithRestaurant = new ArrayList<>();

                        for (DocumentChange document : value.getDocumentChanges()) {
                            if (document.getType() == DocumentChange.Type.ADDED ||
                                    document.getType() == DocumentChange.Type.MODIFIED) {

                                userWithRestaurant.add(document.getDocument().toObject(WorkmateWhoMadeRestaurantChoice.class));

                            }
                        }
                        userModelMutableLiveData.setValue(userWithRestaurant);
                    }
                });
        return userModelMutableLiveData;

    }
}
