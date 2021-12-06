package com.kardabel.go4lunch.usecase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class GetCurrentUserDataUseCase {

    private static final String COLLECTION_USERS = "users";

    // Get the Collection Reference
    private static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // GET USER DATA FROM FIRESTORE
    public static Task<DocumentSnapshot> getUserData() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        return getUsersCollection().document(uid).get();
    }
}
