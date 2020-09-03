package com.aar.app.wsp.data.room;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.annotation.NonNull;

import com.aar.app.wsp.data.xml.WordThemeDataXmlLoader;
import com.aar.app.wsp.model.GameTheme;
import com.aar.app.wsp.model.UsedWord;
import com.aar.app.wsp.model.Word;

import java.util.concurrent.Executors;

@Database(entities = {Word.class, GameTheme.class, UsedWord.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {
    private static final String DB_NAME = "game_data.db";

    private static GameDatabase INSTANCE = null;

    public static GameDatabase getInstance(@NonNull final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, GameDatabase.class, DB_NAME)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            Executors.newSingleThreadScheduledExecutor()
                                    .execute(() -> prepopulateDatabase(context));
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }

    private static void prepopulateDatabase(@NonNull Context context) {
        WordThemeDataXmlLoader dataXmlLoader = new WordThemeDataXmlLoader(context);

        GameDatabase gameDb = GameDatabase.getInstance(context);
        gameDb.getWordDataSource().insertAll(dataXmlLoader.getWords());
        gameDb.getGameThemeDataSource().insertAll(dataXmlLoader.getGameThemes());

        dataXmlLoader.release();
    }

    public abstract WordDataSource getWordDataSource();
    public abstract UsedWordDataSource getUsedWordDataSource();
    public abstract GameThemeDataSource getGameThemeDataSource();
}
