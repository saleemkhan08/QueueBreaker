package com.thnki.queuebreaker.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.listeners.OnDismissListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthDialogFragment extends DialogFragment implements AuthContract.AuthFragmentView {
    public static final String TAG = "AuthDialogFragment";

    @BindView(R.id.userId)
    EditText mUserIdEditText;

    @BindView(R.id.password)
    EditText mPasswordEditText;

    @BindView(R.id.passwordResetErrorMessage)
    TextView passwordResetErrorMessage;

    @BindView(R.id.loginCredentials)
    ViewGroup loginCredentials;

    private OnDismissListener mOnDismissListener;
    private AuthContract.AuthPresenter authPresenter;

    public static AuthDialogFragment getInstance(AuthContract.AuthPresenter presenter) {
        AuthDialogFragment fragment = new AuthDialogFragment();
        fragment.authPresenter = presenter;
        return fragment;
    }

    public AuthDialogFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideWindowTitle();
        View parentView = inflater.inflate(R.layout.auth_dialog_fragment, container, false);
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    private void hideWindowTitle() {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawableResource(R.drawable.login_bg);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(null);
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @OnClick({R.id.facebook_login, R.id.google_login, R.id.twitter_login, R.id.login_button,
            R.id.signup, R.id.forgot_password})
    public void handleClicks(View view) {
        authPresenter.handleClicks(view.getId());
    }


    @Override
    public String getEmailId() {
        return String.valueOf(mUserIdEditText.getText());
    }

    @Override
    public String getPassword() {
        return String.valueOf(mPasswordEditText.getText());
    }

    public void hideKeyboard() {
        Context context = getActivity();
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = getView();
            if (view != null && inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
