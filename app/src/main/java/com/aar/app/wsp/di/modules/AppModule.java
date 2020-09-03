package com.aar.app.wsp.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aar.app.wsp.data.room.GameThemeDataSource;
import com.aar.app.wsp.data.room.UsedWordDataSource;
import com.aar.app.wsp.data.room.WordDataSource;
import com.aar.app.wsp.features.ViewModelFactory;
import com.aar.app.wsp.data.sqlite.GameDataSource;
import com.aar.app.wsp.features.gamehistory.GameHistoryViewModel;
import com.aar.app.wsp.features.gameover.GameOverViewModel;
import com.aar.app.wsp.features.gameplay.GamePlayViewModel;
import com.aar.app.wsp.features.gamethemeselector.ThemeSelectorViewModel;
import com.aar.app.wsp.features.settings.Preferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdularis on 18/07/17.
 */

@Module
public class AppModule {

    private Application mApp;

    public AppModule(Application application) {
        mApp = application;
    }

    @Provides
    @Singleton
    Application provideApp() {
        return mApp;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    // TODO: Optimize this
    @Provides
    ViewModelFactory provideViewModelFactory(Application app,
                                             Preferences preferences,
                                             GameDataSource gameDataSource,
                                             WordDataSource wordDataSource,
                                             UsedWordDataSource usedWordDataSource,
                                             GameThemeDataSource gameThemeDataSource) {
        return new ViewModelFactory(
                new GameOverViewModel(gameDataSource, usedWordDataSource),
                new GamePlayViewModel(gameDataSource, wordDataSource, usedWordDataSource, preferences),
                new GameHistoryViewModel(gameDataSource, preferences),
                new ThemeSelectorViewModel(app, gameThemeDataSource, wordDataSource)
        );
    }
}
