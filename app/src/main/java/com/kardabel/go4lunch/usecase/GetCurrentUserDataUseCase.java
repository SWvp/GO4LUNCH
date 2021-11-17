package com.kardabel.go4lunch.usecase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetCurrentUserDataUseCase {

    private static final String COLLECTION_USERS = "users";

    // Get the Collection Reference
    private static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // GET USER DATA FROM FIRESTORE
    public static Task<DocumentSnapshot> getUserData() {
        String uid = GetCurrentUserIdUseCase.getCurrentUserUID();
        if (uid != null) {
            return getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }
}
