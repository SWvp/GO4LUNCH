package com.kardabel.go4lunch.usecase;

import com.google.firebase.auth.FirebaseUser;

public class GetCurrentUserIdUseCase {

    public static String getCurrentUserUID() {
        FirebaseUser user = GetCurrentUserUseCase.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }
}
