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
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkmatesRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = GetCurrentUserIdUseCase.getCurrentUserUID();


    // GET WORKMATES FROM FIRESTORE DATABASE
    public LiveData<List<UserModel>> getWorkmates() {
        MutableLiveData<List<UserModel>> userModelMutableLiveData = new MutableLiveData<>();

        // WITH SET, WE ENSURE THERE IS NO DUPLICATE, FOR EXAMPLE WHEN ANOTHER USER CHANGE NAME FIELD
        Set<UserModel> workmates = new HashSet<>();

        // TODO : order by "is restaurant been chosen"
        db.collection("users")
                .orderBy("userName")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }
                    for (DocumentChange document : value.getDocumentChanges()) {
                        UserModel usermodel = document.getDocument().toObject(UserModel.class);

                        if (!userId.equals(usermodel.getUid())) {
                            if (document.getType() == DocumentChange.Type.ADDED ||
                                    document.getType() == DocumentChange.Type.MODIFIED) {

                                workmates.add(document.getDocument().toObject(UserModel.class));

                            }
                        }

                    }
                    List<UserModel> workmatesList = new ArrayList<>(workmates);
                    userModelMutableLiveData.setValue(workmatesList);

                });
        return userModelMutableLiveData;
    }
}
