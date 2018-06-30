package com.thnki.queuebreaker.auth;


import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseUser;
import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.generic.LocalRepository;
import com.thnki.queuebreaker.model.Progress;
import com.thnki.queuebreaker.model.Snack;

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
                activityView.launchMainActivity();
                if (isValidateCredentials()) {
                    Progress.show(R.string.signing_up);
                    activityView.signUpWithEmail(email, password);
                }
                break;
            case R.id.forgot_password:
                if(isValidEmail())
                {
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
    public void onAuthStateChanged(FirebaseUser currentUser) {
        if (currentUser != null) {
            localRepository.saveLoginStatus(true);
            activityView.launchMainActivity();
        } else {
            localRepository.saveLoginStatus(false);
        }
    }

//    @Override
//    public void onComplete(@NonNull Task<AuthResult> task) {
//        if (task.isSuccessful()) {
//
//            activityView.launchMainActivity();
//        } else {
//            Progress.hide();
//            ToastMsg.show(R.string.login_failed);
//        }
//
//    }

    /* private void showPasswordResetResult(int msg) {
        loginCredentials.setVisibility(View.GONE);
        TransitionUtil.defaultTransition(loginCredentials);
        passwordResetErrorMessage.setText(msg);
    }

    private void login() {
        mUserId = mUserIdEditText.getText().toString().trim();
        mPassword = mPasswordEditText.getText().toString().trim();
        if (mUserId.length() < MIN_LENGTH_OF_USER_ID) {
            ToastMsg.show(R.string.validUserIdErrMsg);
        } else if (mPassword.length() < MIN_LENGTH_OF_PASSWORD) {
            ToastMsg.show(R.string.validPasswordErrMsg);
        } else {
            Progress.show(R.string.signing_in);
            mAuth.signInWithEmailAndPassword(mUserId + EMAIL_SUFFIX, mPassword)
                    .addOnCompleteListener((OnCompleteListener<AuthResult>) getActivity());
            dismiss();
        }
    }*/
}
