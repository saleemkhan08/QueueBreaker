package com.thnki.queuebreaker.auth;


public interface AuthContract {

    interface AuthActivityView {
        void launchLoginDialog();

        void launchMainActivity();

        void hideKeyboard();
    }

    interface AuthFragmentView {

        String getEmailId();

        String getPassword();
    }

    interface AuthPresenter {

        void handleClicks(int resId);

        void setFragmentView(AuthContract.AuthFragmentView fragmentView);
    }
}
