package com.kardabel.go4lunch.model;

import androidx.annotation.Nullable;

public class UserModel {

    private String uid;
    private String userName;
    private String userEmail;
    @Nullable
    private String userAvatarURL;

    public UserModel(String uid, String userName, String userEmail, @Nullable String userAvatarURL) {
        this.uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAvatarURL = userAvatarURL;
    }

    public String getUid() { return uid; }

    public String getUserName() { return userName; }

    public String getEmail() { return userEmail; }

    @Nullable
    public String getAvatarURL() { return userAvatarURL; }

}

