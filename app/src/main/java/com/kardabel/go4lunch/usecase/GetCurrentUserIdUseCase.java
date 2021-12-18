package com.kardabel.go4lunch.usecase;

import com.google.firebase.auth.FirebaseAuth;

public class GetCurrentUserIdUseCase {

    public String invoke() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
