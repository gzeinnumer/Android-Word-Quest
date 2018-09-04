package com.aar.app.wordsearch.di.modules;

import android.content.Context;

import com.aar.app.wordsearch.data.sqlite.DbHelper;
import com.aar.app.wordsearch.data.sqlite.GameDataSource;
import com.aar.app.wordsearch.data.sqlite.WordDataSource;
import com.aar.app.wordsearch.data.sqlite.GameThemeDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdularis on 18/07/17.
 */

@Module
public class DataSourceModule {

    @Provides
    @Singleton
    DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }

    @Provides
    @Singleton
    GameDataSource provideGameRoundDataSource(DbHelper dbHelper) {
        return new GameDataSource(dbHelper);
    }

    @Provides
    @Singleton
    GameThemeDataSource provideGameThemeDataSource(DbHelper dbHelper) {
        return new GameThemeDataSource(dbHelper);
    }

    @Provides
    @Singleton
    WordDataSource provideWordDataSource(DbHelper dbHelper) {
        return new WordDataSource(dbHelper);
    }

}
