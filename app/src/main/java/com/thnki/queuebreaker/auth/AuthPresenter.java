package com.thnki.queuebreaker.auth;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.generic.LocalRepository;
import com.thnki.queuebreaker.model.Progress;
import com.thnki.queuebreaker.model.Snack;
import com.thnki.queuebreaker.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class AuthPresenter implements AuthContract.AuthPresenter {

    private AuthContract.AuthActivityView activityView;
    private AuthContract.AuthFragmentView fragmentView;

    private LocalRepository localRepository;
    private String email;
    private String password;

    @Inject
    AuthPresenter(AuthContract.AuthActivityView activityView, LocalRepository localRepository) {
        this.activityView = activityView;
        this.localRepository = localRepository;
    }

    @Override
    public void handleClicks(int resId) {
        Log.d("FBLoginSequence", "presenter : handleClicks");
        activityView.hideKeyboard();
        switch (resId) {
            case R.id.auth_dialog:
                activityView.launchLoginDialog();
                break;
            case R.id.facebook_login:
                activityView.setUpFacebookLogin();
                break;
            case R.id.google_login:
                Progress.show(R.string.logging_in);
                activityView.setUpGoogleSignIn();
                break;
            case R.id.twitter_login:
                Progress.show(R.string.logging_in);
                activityView.setUpTwitterLogin();
                break;
            case R.id.login_button:
                if (isValidateCredentials()) {
                    Progress.show(R.string.logging_in);
                    activityView.loginWithEmail(email, password);
                }
                break;
            case R.id.signup:
                if (isValidateCredentials()) {
                    Progress.show(R.string.signing_up);
                    activityView.signUpWithEmail(email, password);
                }
                break;
            case R.id.forgot_password:
                if (isValidEmail()) {
                    activityView.sendPasswordResetEmail(email);
                }
                break;
        }
    }

    private boolean isValidateCredentials() {
        return isValidEmail() && isValidPassword();
    }

    private boolean isValidEmail() {
        email = fragmentView.getEmailId();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snack.show(R.string.invalid_email_id);
            return false;
        }
        return true;
    }

    private boolean isValidPassword() {
        password = fragmentView.getPassword();
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            Snack.show(R.string.password_strength_message);
            return false;
        }
        return true;

    }

    @Override
    public void setFragmentView(AuthContract.AuthFragmentView fragmentView) {
        this.fragmentView = fragmentView;
    }


    @Override
    public void onAuthStateChanged(final FirebaseUser currentUser) {
        Log.d("LaunchedFrom", "currentUser = " + currentUser);
        if (currentUser != null) {
            localRepository.saveLoginStatus(true);
            updateUserDetails(currentUser);
        } else {
            localRepository.saveLoginStatus(false);
        }
    }

    private void updateUserDetails(FirebaseUser currentUser) {
        Log.d("LaunchedFrom", "updateUserDetails");
        final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child(User.USERS).child(currentUser.getUid());
        currentUserRef.addListenerForSingleValueEvent(getValueEventListener(currentUser, currentUserRef));
    }

    @NonNull
    private ValueEventListener getValueEventListener(FirebaseUser currentUser, DatabaseReference currentUserRef) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AuthActivity.currentUser = dataSnapshot.getValue(User.class);
                Log.d("LaunchedFrom", "onDataChange - AuthActivity.currentUser : "
                        + AuthActivity.currentUser);
                if (AuthActivity.currentUser == null) {
                    currentUserRef.setValue(new User(currentUser))
                            .addOnSuccessListener(aVoid -> activityView.launchMainActivity());
                } else {
                    activityView.launchMainActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LaunchedFrom", "onCancelled");
            }
        };
    }

    @Override
    public void setupInitialView() {
        Log.d("LaunchedFrom", "setupInitialView : LoginStatus : " + localRepository.getLoginStatus());
        if (localRepository.getLoginStatus()) {
            activityView.hideLoginDialogButton(View.GONE);
        }
    }
}
