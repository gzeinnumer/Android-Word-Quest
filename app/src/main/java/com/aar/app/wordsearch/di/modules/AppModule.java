package com.aar.app.wordsearch.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aar.app.wordsearch.data.room.GameThemeDataSource;
import com.aar.app.wordsearch.data.room.WordDataSource;
import com.aar.app.wordsearch.features.ViewModelFactory;
import com.aar.app.wordsearch.data.sqlite.GameDataSource;
import com.aar.app.wordsearch.features.gamehistory.GameHistoryViewModel;
import com.aar.app.wordsearch.features.gameover.GameOverViewModel;
import com.aar.app.wordsearch.features.gameplay.GamePlayViewModel;
import com.aar.app.wordsearch.features.gamethemeselector.ThemeSelectorViewModel;

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
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(GameDataSource gameDataSource,
                                             WordDataSource wordDataSource,
                                             GameThemeDataSource gameThemeDataSource) {
        return new ViewModelFactory(
                new GameOverViewModel(gameDataSource),
                new GamePlayViewModel(gameDataSource, wordDataSource),
                new GameHistoryViewModel(gameDataSource),
                new ThemeSelectorViewModel(gameThemeDataSource)
        );
    }
}
