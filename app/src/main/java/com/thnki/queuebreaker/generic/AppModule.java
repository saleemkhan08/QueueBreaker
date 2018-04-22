package com.thnki.queuebreaker.generic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @AppScope
    SharedPreferences sharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @AppScope
    Context context() {
        return context;
    }

    @Provides
    @AppScope
    ImagePipelineConfig config(Context context) {
        return ImagePipelineConfig.newBuilder(context)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();
    }

    @Provides
    @AppScope
    LocalRepository localRepository(SharedPreferences preferences) {
        return new LocalRepository(preferences);
    }

    @Provides
    @AppScope
    ConnectivityUtil connectivityUtil(Context context) {
        return new ConnectivityUtil(context);
    }
}
