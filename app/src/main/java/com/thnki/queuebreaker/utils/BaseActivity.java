package com.thnki.queuebreaker.utils;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.model.Progress;
import com.thnki.queuebreaker.model.Snack;
import com.thnki.queuebreaker.model.ToastMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreCreateView(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(getContentView());
        onPostCreateView(savedInstanceState);
    }

    @SuppressWarnings("unused")
    protected void onPostCreateView(Bundle savedInstanceState) {

    }

    @SuppressWarnings("unused")
    protected void onPreCreateView(Bundle savedInstanceState) {

    }

    abstract protected int getContentView();

    @Subscribe
    public void snackBar(Snack snack) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), snack.getMsg(), Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        TextView textView = layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.setBackgroundResource(R.color.colorPrimary);
        snackbar.show();
    }

    @Subscribe
    public void toastMsg(ToastMsg toast) {
        if (toast.getTxtMsg() == null) {
            Toast.makeText(this, toast.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, toast.getTxtMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void progressDialog(Progress progress) {
        if (progress.toBeShown()) {
            showProgressDialog(progress.getMsg());
        } else {
            hideProgressDialog();
        }
    }

    protected void showProgressDialog(int msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.setMessage(getString(msg));
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
