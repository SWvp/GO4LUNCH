package com.kardabel.go4lunch.usecase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kardabel.go4lunch.model.UserModel;

public class CreateUserUseCase {

    private static final String COLLECTION_USERS = "users";

    private static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // CREATE CURRENT USER IN FIRESTORE
    public static void createUser() {
        FirebaseUser user = GetCurrentUserUseCase.invoke();
        if (user != null) {
            String userUrlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String userName = user.getDisplayName();
            String uid = user.getUid();
            String userEmail = user.getEmail();

            UserModel userToCreate = new UserModel(uid, userName, userUrlPicture, userEmail);

            Task<DocumentSnapshot> userData = GetCurrentUserDataUseCase.getUserData();
            // If the user already exist in Firestore, we get his data
            userData.addOnSuccessListener(documentSnapshot -> {
                getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }
}
