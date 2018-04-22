package com.thnki.queuebreaker.auth;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.generic.LocalRepository;
import com.thnki.queuebreaker.model.Progress;
import com.thnki.queuebreaker.model.ToastMsg;

import javax.inject.Inject;

public class AuthPresenter implements AuthContract.AuthPresenter, OnCompleteListener<AuthResult> {

    private AuthContract.AuthActivityView activityView;
    private AuthContract.AuthFragmentView fragmentView;

    private LocalRepository localRepository;

    @Inject
    AuthPresenter(AuthContract.AuthActivityView activityView, LocalRepository localRepository) {
        this.activityView = activityView;
        this.localRepository = localRepository;
    }

    @Override
    public void handleClicks(int resId) {
        activityView.hideKeyboard();
        switch (resId) {
            case R.id.auth_dialog:
                activityView.launchLoginDialog();
                break;
            case R.id.facebook_login:
                ToastMsg.show(R.string.facebook_login);
                break;
            case R.id.google_login:
                ToastMsg.show(R.string.google_login);
                break;
            case R.id.twitter_login:
                ToastMsg.show(R.string.twitter_login);
                break;
            case R.id.login_button:
                Progress.show(R.string.logging_in);
                activityView.launchMainActivity();
                break;
            case R.id.signup:
                ToastMsg.show(R.string.signup);
                break;
            case R.id.forgot_password:
                ToastMsg.show(R.string.reset_password);
                sendPasswordResetEmail();
                break;
        }
    }

    @Override
    public void setFragmentView(AuthContract.AuthFragmentView fragmentView) {
        this.fragmentView = fragmentView;
    }

    private void sendPasswordResetEmail() {
        final String userId = fragmentView.getEmailId();
        if (TextUtils.isEmpty(userId)) {
            ToastMsg.show(R.string.validUserIdErrMsg);
        } else {
            Progress.show(R.string.please_wait);
            //TODO
        }
        Progress.hide();
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            localRepository.saveLoginStatus(true);
            activityView.launchMainActivity();
        } else {
            localRepository.saveLoginStatus(false);
            Progress.hide();
            ToastMsg.show(R.string.loginFailed);
        }

    }

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
