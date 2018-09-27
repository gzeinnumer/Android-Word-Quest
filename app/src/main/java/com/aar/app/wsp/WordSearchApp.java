package com.aar.app.wsp;

import android.app.Application;

import com.aar.app.wsp.di.component.AppComponent;
import com.aar.app.wsp.di.component.DaggerAppComponent;
import com.aar.app.wsp.di.modules.AppModule;

/**
 * Created by abdularis on 18/07/17.
 */

public class WordSearchApp extends Application {

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
