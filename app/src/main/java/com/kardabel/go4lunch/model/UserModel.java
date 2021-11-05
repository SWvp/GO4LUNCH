package com.kardabel.go4lunch.model;

import androidx.annotation.Nullable;

public class UserModel {

    private String uid;
    private String userName;
    @Nullable
    private String avatarURL;
    private String email;

    public UserModel() {
    }


    public UserModel(String uid, String userName, @Nullable String avatarURL, String email) {
        this.uid = uid;
        this.userName = userName;
        this.avatarURL = avatarURL;
        this.email = email;
    }

    public String getUid() { return uid; }

    public String getUserName() { return userName; }

    public String getEmail() { return email; }

    @Nullable
    public String getAvatarURL() { return avatarURL; }

}

