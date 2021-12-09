package com.kardabel.go4lunch.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class GetCurrentUserIdUseCase {

    public String invoke() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
