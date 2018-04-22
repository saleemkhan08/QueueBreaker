package com.thnki.queuebreaker.generic;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.imagepipeline.core.ImagePipelineConfig;

import dagger.Component;

@AppScope
@Component(modules = AppModule.class)
public interface AppComponent {

    Context getContext();

    SharedPreferences getSharedPreferences();

    ImagePipelineConfig getFrescoConfig();

    LocalRepository getLocalRepository();

    ConnectivityUtil getConnectivityUtil();
}
