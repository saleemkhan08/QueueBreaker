package com.thnki.queuebreaker.auth;


import com.google.firebase.auth.FirebaseUser;

public interface AuthContract {

    interface AuthActivityView {
        void launchLoginDialog();

        void launchMainActivity();

        void hideKeyboard();

        void setUpFacebookLogin();

        void setUpGoogleSignIn();

        void setUpTwitterLogin();

        void loginWithEmail(String email, String password);

        void signUpWithEmail(String email, String password);

        void sendPasswordResetEmail(String email);
    }

    interface AuthFragmentView {

        String getEmailId();

        String getPassword();

    }

    interface AuthPresenter {

        void handleClicks(int resId);

        void setFragmentView(AuthContract.AuthFragmentView fragmentView);

        void onAuthStateChanged(FirebaseUser currentUser);
    }
}
