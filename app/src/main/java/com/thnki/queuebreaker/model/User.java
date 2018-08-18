package com.thnki.queuebreaker.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;

public class User {

    public static final String CUSTOMER = "customer";
    public static final String WAITER = "staff";
    public static final String ADMIN = "admin";
    public static final String COOK = "cook";

    private static final String NOT_SET = "not set";
    public static final String USERS = "users";
    private String userId;
    private String fullName;
    private String photoUrl;
    private String password;
    private String email;
    private String phoneNumber;
    private String token;
    private String userType;

    public User() {

    }

    public User(FirebaseUser user) {
        userId = user.getUid();
        fullName = user.getDisplayName();
        photoUrl = String.valueOf(user.getPhotoUrl());
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        token = "";
        password = "";
        userType = CUSTOMER;

        userId = user.getUid();
    }

    public String getPhoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = NOT_SET;
        }
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        if (email == null) {
            email = NOT_SET;
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        if (fullName == null || TextUtils.isEmpty(fullName)) {
            return "Name isn't updated..";
        }
        return fullName;
    }

    public String getPhotoUrl() {
        if (photoUrl == null) {
            return "";
        }
        return photoUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).getUserId().equals(getUserId());
    }

    @Override
    public String toString() {
        return getUserId() + " : " + getFullName();
    }

    public boolean validateEmail() {
        return !TextUtils.isEmpty(email) || !email.equals(User.NOT_SET) ||
                (email.matches("^[0-9a-zA-Z!#$%&;'*+\\-/\\=\\?\\^_`\\.{|}~]{1,64}@[0-9a-zA-Z]{1,255}\\.[a-zA-Z]{1,10}")
                        && email.length() <= 320);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


}