package com.thnki.queuebreaker.generic;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thnki.queuebreaker.R;

public class NetworkStateActivity extends AppCompatActivity implements Animator.AnimatorListener {

    public static final String NETWORK_STATUS = "networkStatus";
    private boolean animation_has_ended = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_state);
        getWindow().setBackgroundDrawableResource(R.drawable.login_bg);
        setFinishOnTouchOutside(false);
        closeActivity(getIntent());
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onBackPressed() {

        if (animation_has_ended) {
            final float FREQ = 3f;
            final float DECAY = 2f;
            TimeInterpolator decayingSineWave = new TimeInterpolator() {
                @Override
                public float getInterpolation(float input) {
                    double raw = Math.sin(FREQ * input * 2 * Math.PI);
                    return (float) (raw * Math.exp(-input * DECAY));
                }
            };

            findViewById(R.id.no_internet_icon).animate()
                    .setListener(this)
                    .xBy(-100)
                    .setInterpolator(decayingSineWave)
                    .setDuration(500)
                    .start();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        closeActivity(intent);
    }

    private void closeActivity(Intent intent) {
        if (intent.getBooleanExtra(NETWORK_STATUS, false)) {
            finish();
        }
    }

    @Override
    public void onAnimationStart(Animator animator) {
        animation_has_ended = false;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        animation_has_ended = true;
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
