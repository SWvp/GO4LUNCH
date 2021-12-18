package com.kardabel.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kardabel.go4lunch.model.UserModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkmatesRepository {

    public static final String USERS = "users";
    public static final String USER_NAME = "userName";

    // GET WORKMATES FROM FIRESTORE DATABASE
    public LiveData<List<UserModel>> getWorkmates() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        MutableLiveData<List<UserModel>> userModelMutableLiveData = new MutableLiveData<>();

        // WITH SET, WE ENSURE THERE IS NO DUPLICATE, FOR EXAMPLE WHEN ANOTHER USER CHANGE NAME FIELD
        Set<UserModel> workmates = new HashSet<>();

        db.collection(USERS)
                .orderBy(USER_NAME)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }
                    assert value != null;
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
