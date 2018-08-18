package com.thnki.queuebreaker.auth;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.thnki.queuebreaker.QueueBreaker;
import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.generic.AppComponent;
import com.thnki.queuebreaker.home.HomeActivity;
import com.thnki.queuebreaker.listeners.OnDismissListener;
import com.thnki.queuebreaker.model.Progress;
import com.thnki.queuebreaker.model.Snack;
import com.thnki.queuebreaker.model.User;
import com.thnki.queuebreaker.utils.BaseActivity;
import com.thnki.queuebreaker.utils.SquareImageView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthActivity extends BaseActivity implements
        OnDismissListener,
        AuthContract.AuthActivityView,
        FirebaseAuth.AuthStateListener,
        GoogleApiClient.OnConnectionFailedListener, OnCompleteListener<AuthResult>, FacebookCallback<LoginResult> {
    public static final String LOGIN_STATUS = "loginStatus";
    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 12;

    //keytool -exportcert -alias androidreleasekey -keystore /Users/saleem/Downloads/thnki.com.jks | openssl sha1 -binary | openssl base64

    @Inject
    AuthSectionsPagerAdapter sectionsPagerAdapter;

    @Inject
    AuthContract.AuthPresenter authPresenter;

    @BindView(R.id.loginContainer)
    View mLoginButtonContainer;

    @BindView(R.id.pageIndicatorViewContainer)
    FrameLayout mPageIndicatorViewContainer;

    @BindView(R.id.pageIndicatorView)
    PageIndicatorView mPageIndicatorView;

    @BindView(R.id.title)
    TextView appName;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.walk_through_image)
    SquareImageView walkThroughImageView;

    @BindView(R.id.walk_through_description)
    TextView descriptionView;

    private boolean isActivityRunning;
    private AuthDialogFragment authDialogFragment;
    private CallbackManager mCallbackManager;
    private FirebaseAuth auth;
    private TwitterAuthClient client;
    public static User currentUser;

    @Override
    protected void onPostCreateView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        configureDependencies();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Gabriola.ttf");
        appName.setTypeface(typeface);
        viewPager.setAdapter(sectionsPagerAdapter);
        authPresenter.setupInitialView();
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);
        Log.d("AuthActivityProblem", "onPostCreateView");

        descriptionView.setTypeface(typeface);
        descriptionView.setVisibility(View.INVISIBLE);
        walkThroughImageView.setVisibility(View.INVISIBLE);
        mPageIndicatorViewContainer.setVisibility(View.VISIBLE);
        mPageIndicatorView.setViewPager(viewPager);
        mPageIndicatorView.setAnimationType(AnimationType.THIN_WORM);
        mPageIndicatorView.setSelectedColor(ContextCompat.getColor(this, R.color.colorAccent));
        mPageIndicatorView.setUnselectedColor(ContextCompat.getColor(this, R.color.gray));
        mPageIndicatorView.setInteractiveAnimation(true);
    }

    @Override
    protected int getContentView() {
        return R.layout.auth_activity;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
        Log.d("AuthActivityProblem", "onStart");
    }

    private void configureDependencies() {
        Log.d("AuthActivityProblem", "configureDependencies");
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
        Log.d("AuthActivityProblem", "onDestroy");
        isActivityRunning = false;
        hideProgressDialog();
    }

    @OnClick(R.id.auth_dialog)
    public void handleClicks(View view) {
        Log.d("AuthActivityProblem", "handleClicks");
        authPresenter.handleClicks(view.getId());
    }

    @Override
    public void launchLoginDialog() {
        Log.d("AuthActivityProblem", "launchLoginDialog");
        FragmentManager manager = getSupportFragmentManager();
        authDialogFragment = AuthDialogFragment.getInstance(authPresenter);
        authDialogFragment.setOnDismissListener(this);
        authDialogFragment.show(manager, AuthDialogFragment.TAG);
        authPresenter.setFragmentView(authDialogFragment);
        hideLoginDialogButton(View.GONE);
    }

    @Override
    public void hideLoginDialogButton(int visibility) {
        Log.d("AuthActivityProblem", "hideLoginDialogButton");
        mLoginButtonContainer.setVisibility(visibility);
        mPageIndicatorView.setVisibility(visibility);
    }

    @Override
    public void onDismiss(String msg) {
        Log.d("AuthActivityProblem", "onDismiss");
        hideLoginDialogButton(View.VISIBLE);
    }

    @Override
    public void launchMainActivity() {
        Log.d("AuthActivityProblem", "Activity View: launchMainActivity");
        if (isActivityRunning) {
            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
            finish();
        }
    }

    public void launchMainActivityWithDelay() {
        Log.d("AuthActivityProblem", "Activity View: launchMainActivityWithDelay");
        hideLoginDialogButton(View.GONE);
        new Handler().postDelayed(() -> {
            if (isActivityRunning) {
                startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                finish();
            }
        }, 1000);
    }

    @Override
    public void hideKeyboard() {
        Log.d("AuthActivityProblem", "hideKeyboard");
        if (authDialogFragment != null)
            authDialogFragment.hideKeyboard();
    }

    @Override
    public void setUpGoogleSignIn() {
        Log.d("AuthActivityProblem", "setUpGoogleSignIn");
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void setUpTwitterLogin() {
        // Configure Twitter SDK
        Log.d("AuthActivityProblem", "setUpTwitterLogin");
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);
        client = new TwitterAuthClient();
        client.authorize(AuthActivity.this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                auth.signInWithCredential(TwitterAuthProvider.getCredential(
                        twitterSessionResult.data.getAuthToken().token,
                        twitterSessionResult.data.getAuthToken().secret
                )).addOnCompleteListener(AuthActivity.this);
            }

            @Override
            public void failure(TwitterException e) {
                Snack.show(R.string.login_failed);
            }
        });
    }

    @Override
    public void setUpFacebookLogin() {
        Log.d("AuthActivityProblem", "Activity View : setUpFacebookLogin");
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        loginManager.registerCallback(mCallbackManager, this);
    }

    @Override
    public void loginWithEmail(String email, String password) {
        Log.d("AuthActivityProblem", "loginWithEmail");
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this);
    }

    @Override
    public void signUpWithEmail(String email, String password) {
        Log.d("AuthActivityProblem", "signUpWithEmail");
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this);
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        Log.d("AuthActivityProblem", "sendPasswordResetEmail");
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            Progress.hide();
            if (task.isSuccessful()) {
                Snack.show(R.string.mail_sent);
            } else {
                Snack.show(R.string.please_check_your_email_and_try_again);
            }

            if (task.isCanceled()) {
                Snack.show(R.string.password_reset_cancllled);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AuthActivityProblem", "onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                firebaseAuthWithGoogle(task.getResult(ApiException.class));
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            if (mCallbackManager != null) {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            } else if (client != null) {
                client.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this);
        Log.d("AuthActivityProblem", "firebaseAuthWithGoogle");
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        authPresenter.onAuthStateChanged(firebaseAuth.getCurrentUser());
        Log.d("AuthActivityProblem", "onAuthStateChanged");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Progress.hide();
        Snack.show(R.string.login_failed);
        Log.d("AuthActivityProblem", "onConnectionFailed");
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d("AuthActivityProblem", "onComplete");
        if (!task.isSuccessful()) {
            Progress.hide();
            Snack.show(R.string.login_failed);
        } else if (task.isCanceled()) {
            Progress.hide();
            Snack.show(R.string.login_cancelled);
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d("AuthActivityProblem", "onSuccess");
        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
        Progress.show(R.string.logging_in);
        auth.signInWithCredential(credential).addOnCompleteListener(this);
    }

    @Override
    public void onCancel() {
        Log.d("AuthActivityProblem", "onCancel");
        Progress.hide();
        Snack.show(R.string.login_cancelled);
    }

    @Override
    public void onError(FacebookException error) {
        Log.d("AuthActivityProblem", "onError");
        Progress.hide();
        Snack.show(R.string.login_failed);
    }
}
