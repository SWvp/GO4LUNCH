package com.kardabel.go4lunch.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.usecase.FirestoreUseCase;

public class UserManager {

    private static volatile UserManager instance;
    private FirestoreUseCase mFirestoreUseCase;

    private UserManager() {
        mFirestoreUseCase = FirestoreUseCase.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(FirestoreUseCase.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public Boolean isCurrentUserLogged(){
        return (mFirestoreUseCase.getCurrentUser() != null);
    }

    public void createUser() {
        mFirestoreUseCase.createUser();
    }

    public FirebaseUser getCurrentUser(){
        return mFirestoreUseCase.getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return mFirestoreUseCase.signOut(context);
    }

    public Task<UserModel> getUserData() {
        // Get the user from Firestore and cast it to a User model Object
        return mFirestoreUseCase.getUserData().continueWith(task -> task.getResult().toObject(UserModel.class));
    }
}
