package com.aar.app.wsp.di.modules

import android.content.Context
import com.aar.app.wsp.data.room.GameDatabase
import com.aar.app.wsp.data.room.GameThemeDataSource
import com.aar.app.wsp.data.room.UsedWordDataSource
import com.aar.app.wsp.data.room.WordDataSource
import com.aar.app.wsp.data.sqlite.DbHelper
import com.aar.app.wsp.data.sqlite.GameDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by abdularis on 18/07/17.
 */
@Module
class DataSourceModule {
    @Provides
    @Singleton
    fun provideGameDatabase(context: Context): GameDatabase {
        return GameDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDbHelper(context: Context): DbHelper {
        return DbHelper(context)
    }

    @Provides
    @Singleton
    fun provideGameRoundDataSource(
        dbHelper: DbHelper,
        usedWordDataSource: UsedWordDataSource
    ): GameDataSource {
        return GameDataSource(dbHelper, usedWordDataSource)
    }

    @Provides
    @Singleton
    fun provideGameThemeDataSource(gameDatabase: GameDatabase): GameThemeDataSource {
        return gameDatabase.gameThemeDataSource
    }

    @Provides
    @Singleton
    fun provideWordDataSource(gameDatabase: GameDatabase): WordDataSource {
        return gameDatabase.wordDataSource
    }

    @Provides
    @Singleton
    fun provideUsedWordDataSource(gameDatabase: GameDatabase): UsedWordDataSource {
        return gameDatabase.usedWordDataSource
    }
}