package com.kardabel.go4lunch.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetCurrentUserUseCase {

    public static FirebaseUser invoke() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
