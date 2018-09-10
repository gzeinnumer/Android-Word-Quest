package com.aar.app.wordsearch.di.modules;

import android.content.Context;

import com.aar.app.wordsearch.data.room.GameDatabase;
import com.aar.app.wordsearch.data.room.GameThemeDataSource;
import com.aar.app.wordsearch.data.room.UsedWordDataSource;
import com.aar.app.wordsearch.data.room.WordDataSource;
import com.aar.app.wordsearch.data.sqlite.DbHelper;
import com.aar.app.wordsearch.data.sqlite.GameDataSource;

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
    GameDatabase provideGameDatabase(Context context) {
        return GameDatabase.getInstance(context);
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }

    @Provides
    @Singleton
    GameDataSource provideGameRoundDataSource(DbHelper dbHelper, UsedWordDataSource usedWordDataSource) {
        return new GameDataSource(dbHelper, usedWordDataSource);
    }

    @Provides
    @Singleton
    GameThemeDataSource provideGameThemeDataSource(GameDatabase gameDatabase) {
        return gameDatabase.getGameThemeDataSource();
    }

    @Provides
    @Singleton
    WordDataSource provideWordDataSource(GameDatabase gameDatabase) {
        return gameDatabase.getWordDataSource();
    }

    @Provides
    @Singleton
    UsedWordDataSource provideUsedWordDataSource(GameDatabase gameDatabase) {
        return gameDatabase.getUsedWordDataSource();
    }
}
