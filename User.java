package com.example.planningmeeting;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
    private String email;
    private String biography;
    private String photoUrl;
    private String password;
    private String login;
    private Map<String, Boolean> following; // Подписки
    private Map<String, Boolean> followers; // Подписчики

    private String background;

    public User() {
        // Пустой конструктор требуется для Firebase
    }

    public User(String email, String biography, String photoUrl, String login, String password, String background) {
        this.email = email;
        this.biography = biography;
        this.photoUrl = photoUrl;
        this.login = login;
        this.password = password;
        this.following = new HashMap<>();
        this.followers = new HashMap<>();
        this.background = background;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getEmail() {
        return email;
    }

    public String getBiography() {
        return biography;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPassword(){
        return password;
    }

    public String getLogin(){
        return  login;
    }

    public void setSubscriptions(Map<String, Boolean> subscriptions) {
        this.following = subscriptions;
    }

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = followers;
    }

    public Map<String, Boolean> getSubscriptions() {
        return following;
    }

    public Map<String, Boolean> getFollowers() {
        return followers;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    protected User(Parcel in) {
        biography = in.readString();
        login = in.readString();
        following = in.readHashMap(Boolean.class.getClassLoader());
        followers = in.readHashMap(Boolean.class.getClassLoader());
        photoUrl = in.readString();
        background = in.readString();
        password = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(biography);
        dest.writeString(login);
        dest.writeMap(following);
        dest.writeMap(followers);
        dest.writeString(photoUrl);
        dest.writeString(background);
        dest.writeString(password);
        dest.writeString(email);
    }
}
