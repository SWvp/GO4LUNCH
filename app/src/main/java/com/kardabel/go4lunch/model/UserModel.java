package com.kardabel.go4lunch.model;

import androidx.annotation.Nullable;

public class UserModel {

    private final String uid;
    private final String userName;
    @Nullable
    private final String userAvatarURL;
    private final String userEmail;

    public UserModel(String uid, String userName, @Nullable String userAvatarURL, String userEmail) {
        this.uid = uid;
        this.userName = userName;
        this.userAvatarURL = userAvatarURL;
        this.userEmail = userEmail;
    }

    public String getUid() { return uid; }

    public String getUserName() { return userName; }

    public String getEmail() { return userEmail; }

    @Nullable
    public String getAvatarURL() { return userAvatarURL; }

}

