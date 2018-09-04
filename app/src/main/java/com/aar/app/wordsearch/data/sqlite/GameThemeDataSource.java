package com.aar.app.wordsearch.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aar.app.wordsearch.data.sqlite.DbContract;
import com.aar.app.wordsearch.data.sqlite.DbHelper;
import com.aar.app.wordsearch.model.GameTheme;

import java.util.ArrayList;
import java.util.List;

public class GameThemeDataSource {

    private DbHelper mHelper;

    public GameThemeDataSource(DbHelper dbHelper) {
        mHelper = dbHelper;
    }

    public List<GameTheme> getThemes() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String cols[] = {
                DbContract.Theme._ID,
                DbContract.Theme.COL_NAME
        };

        Cursor c = db.query(DbContract.Theme.TABLE_NAME, cols, null, null, null, null, null);

        List<GameTheme> gameThemes = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                GameTheme theme = new GameTheme(c.getInt(0), c.getString(1));
                gameThemes.add(theme);
                c.moveToNext();
            }
        }
        c.close();
        return gameThemes;
    }

//    public void insertAll(List<GameTheme> gameThemes) {
//        SQLiteDatabase db = mHelper.getWritableDatabase();
//        ContentValues vals = new ContentValues();
//
//        for (GameTheme gameTheme : gameThemes) {
//            vals.clear();
//            vals.put(DbContract.Theme.COL_NAME, gameTheme.getName());
//            long gtid = db.insert(DbContract.Theme.TABLE_NAME, "null", vals);
//            gameTheme.setId((int) gtid);
//        }
//    }
}
