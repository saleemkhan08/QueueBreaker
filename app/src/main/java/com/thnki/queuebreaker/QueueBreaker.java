package com.thnki.queuebreaker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.thnki.queuebreaker.generic.AppComponent;
import com.thnki.queuebreaker.generic.AppModule;
import com.thnki.queuebreaker.generic.DaggerAppComponent;
import com.thnki.queuebreaker.generic.NetworkStateReceiver;

public class QueueBreaker extends MultiDexApplication {

    AppComponent component;
    private BroadcastReceiver networkStateReceiver;

    public static QueueBreaker getApp(Activity activity) {
        return (QueueBreaker) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext())).build();

        Fresco.initialize(this, component.getFrescoConfig());

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        IntentFilter intentFilter = new IntentFilter(NetworkStateReceiver.CONNECTIVITY_CHANGE);
        networkStateReceiver = new NetworkStateReceiver();
        registerReceiver(networkStateReceiver, intentFilter);
    }

    public AppComponent getComponent() {
        return component;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(networkStateReceiver);
    }
}