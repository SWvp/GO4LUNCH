package com.kardabel.go4lunch.usecase;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kardabel.go4lunch.model.UserModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FirestoreUseCase {

    private static final String COLLECTION_USERS = "users";
    private static final String USERNAME_FIELD = "username";

    private static volatile FirestoreUseCase instance;

    public FirestoreUseCase() {
    }

    public static FirestoreUseCase getInstance() {
        FirestoreUseCase result = instance;
        if (result != null) {
            return result;
        }
        synchronized (FirestoreUseCase.class) {
            if (instance == null) {
                instance = new FirestoreUseCase();
            }
            return instance;
        }
    }

    @Nullable
    public String getCurrentUserUID() {
        FirebaseUser user = GetCurrentUserUseCase.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    // Get the Collection Reference
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // UPDATE USERS NAME
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        } else {
            return null;
        }
    }

    // DELETE USER FROM FIRESTORE
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }
}
