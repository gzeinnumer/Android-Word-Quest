package com.aar.app.wsp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aar.app.wsp.data.xml.WordThemeDataXmlLoader
import com.aar.app.wsp.model.GameTheme
import com.aar.app.wsp.model.UsedWord
import com.aar.app.wsp.model.Word
import java.util.concurrent.Executors

@Database(entities = [Word::class, GameTheme::class, UsedWord::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract val wordDataSource: WordDataSource
    abstract val usedWordDataSource: UsedWordDataSource
    abstract val gameThemeDataSource: GameThemeDataSource

    companion object {
        private const val DB_NAME = "game_data.db"
        private var INSTANCE: GameDatabase? = null
        fun getInstance(context: Context): GameDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, GameDatabase::class.java, DB_NAME)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            Executors.newSingleThreadScheduledExecutor()
                                .execute { prepopulateDatabase(context) }
                        }
                    })
                    .build()
            }
            return INSTANCE!!
        }

        private fun prepopulateDatabase(context: Context) {
            val dataXmlLoader = WordThemeDataXmlLoader(context)
            val gameDb = getInstance(context)
            gameDb.wordDataSource.insertAll(dataXmlLoader.words)
            gameDb.gameThemeDataSource.insertAll(dataXmlLoader.gameThemes)
            dataXmlLoader.release()
        }
    }
}