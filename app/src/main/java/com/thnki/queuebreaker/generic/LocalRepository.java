package com.thnki.queuebreaker.generic;

import android.content.SharedPreferences;

import static com.thnki.queuebreaker.auth.AuthActivity.LOGIN_STATUS;

public class LocalRepository {

    SharedPreferences preferences;

    public LocalRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void saveLoginStatus(boolean status) {
        preferences.edit()
                .putBoolean(LOGIN_STATUS, status)
                .apply();
    }

    public boolean getLoginStatus() {
        return preferences.getBoolean(LOGIN_STATUS, false);
    }

}
