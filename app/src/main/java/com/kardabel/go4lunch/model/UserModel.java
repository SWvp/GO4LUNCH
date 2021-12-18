package com.kardabel.go4lunch.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(uid, userModel.uid) && Objects.equals(userName, userModel.userName) && Objects.equals(avatarURL, userModel.avatarURL) && Objects.equals(email, userModel.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, userName, avatarURL, email);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserModel{" +
                "uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

