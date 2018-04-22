package com.thnki.queuebreaker.auth;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.thnki.queuebreaker.QueueBreaker;
import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.generic.AppComponent;
import com.thnki.queuebreaker.home.MainActivity;
import com.thnki.queuebreaker.listeners.OnDismissListener;
import com.thnki.queuebreaker.utils.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthActivity extends BaseActivity implements OnDismissListener, AuthContract.AuthActivityView {
    public static final String LOGIN_STATUS = "loginStatus";

    @Inject
    AuthSectionsPagerAdapter sectionsPagerAdapter;

    @Inject
    AuthContract.AuthPresenter authPresenter;

    @BindView(R.id.loginContainer)
    View mLoginButtonContainer;

    @BindView(R.id.pageIndicatorView)
    View mPageIndicatorView;

    @BindView(R.id.title)
    TextView appName;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private boolean isActivityRunning;
    private AuthDialogFragment authDialogFragment;

    @Override
    protected void onPostCreateView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        configureDependencies();
        appName.setTypeface(Typeface.createFromAsset(getAssets(), "Gabriola.ttf"));
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    protected int getContentView() {
        return R.layout.auth_activity;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    private void configureDependencies() {
        AppComponent appComponent = QueueBreaker.getApp(this).getComponent();

        AuthComponent component = DaggerAuthComponent.builder()
                .authModule(new AuthModule(this))
                .appComponent(appComponent)
                .build();
        component.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
        hideProgressDialog();
    }

    @OnClick(R.id.auth_dialog)
    public void handleClicks(View view) {
        authPresenter.handleClicks(view.getId());
    }

    @Override
    public void launchLoginDialog() {
        FragmentManager manager = getSupportFragmentManager();
        authDialogFragment = AuthDialogFragment.getInstance(authPresenter);
        authDialogFragment.setOnDismissListener(this);
        authDialogFragment.show(manager, AuthDialogFragment.TAG);
        mLoginButtonContainer.setVisibility(View.GONE);
        authPresenter.setFragmentView(authDialogFragment);
    }

    @Override
    public void onDismiss(String msg) {
        mLoginButtonContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void launchMainActivity() {
        mLoginButtonContainer.setVisibility(View.GONE);
        mPageIndicatorView.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isActivityRunning) {
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 1000);
    }

    @Override
    public void hideKeyboard() {
        /* From activity Keyboard is not getting hidden */
        if (authDialogFragment != null)
            authDialogFragment.hideKeyboard();
    }
}
