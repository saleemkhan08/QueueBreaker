package com.thnki.queuebreaker.auth;

import com.thnki.queuebreaker.generic.LocalRepository;

import dagger.Module;
import dagger.Provides;

@Module
class AuthModule {

    private AuthContract.AuthActivityView activityView;
    private AuthActivity authActivity;

    AuthModule(AuthContract.AuthActivityView activityView) {

        this.activityView = activityView;
        authActivity = (AuthActivity) activityView;
    }

    @Provides
    @AuthScope
    AuthContract.AuthPresenter getAuthPresenter(LocalRepository repository) {
        return new AuthPresenter(activityView, repository);
    }

    @Provides
    @AuthScope
    AuthSectionsPagerAdapter getPagerAdapter() {
        return new AuthSectionsPagerAdapter(authActivity.getSupportFragmentManager());
    }

    @Provides
    @AuthScope
    AuthContract.AuthActivityView getActivityView() {
        return activityView;
    }

}
