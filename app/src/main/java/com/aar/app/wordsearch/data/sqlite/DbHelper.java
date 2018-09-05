package com.aar.app.wordsearch.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aar.app.wordsearch.data.xml.WordThemeDataXmlLoader;
import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.model.Word;

/**
 * Created by abdularis on 18/07/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_TABLE_THEME =
            "CREATE TABLE " + DbContract.Theme.TABLE_NAME + " (" +
                    DbContract.Theme._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.Theme.COL_NAME + " TEXT)";

    private static final String SQL_CREATE_TABLE_WORDS =
            "CREATE TABLE " + DbContract.WordBank.TABLE_NAME + " (" +
                    DbContract.WordBank._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.WordBank.COL_GAME_THEME_ID + " INTEGER," +
                    DbContract.WordBank.COL_STRING + " TEXT," +
                    DbContract.WordBank.COL_SUB_STRING + " TEXT)";

    private static final String SQL_CREATE_TABLE_USED_WORD =
            "CREATE TABLE " + DbContract.UsedWord.TABLE_NAME + " (" +
                    DbContract.UsedWord._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.UsedWord.COL_GAME_ROUND_ID + " INTEGER," +
                    DbContract.UsedWord.COL_STRING + " TEXT," +
                    DbContract.UsedWord.COL_SUB_STRING + " TEXT," +
                    DbContract.UsedWord.COL_ANSWER_LINE_DATA + " TEXT," +
                    DbContract.UsedWord.COL_LINE_COLOR + " INTEGER)";

    private static final String SQL_CREATE_TABLE_GAME_ROUND =
            "CREATE TABLE " + DbContract.GameRound.TABLE_NAME + " (" +
                    DbContract.GameRound._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.GameRound.COL_NAME + " TEXT," +
                    DbContract.GameRound.COL_DURATION + " INTEGER," +
                    DbContract.GameRound.COL_GRID_ROW_COUNT + " INTEGER," +
                    DbContract.GameRound.COL_GRID_COL_COUNT + " INTEGER," +
                    DbContract.GameRound.COL_GRID_DATA + " TEXT)";

    private WordThemeDataXmlLoader mWordThemeDataXmlLoader;
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mWordThemeDataXmlLoader = new WordThemeDataXmlLoader(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_THEME);
        db.execSQL(SQL_CREATE_TABLE_WORDS);
        db.execSQL(SQL_CREATE_TABLE_USED_WORD);
        db.execSQL(SQL_CREATE_TABLE_GAME_ROUND);

        prepopulateGameThemes(db);
        prepopulateWordsData(db);
        mWordThemeDataXmlLoader.release();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void prepopulateGameThemes(SQLiteDatabase db) {
        ContentValues vals = new ContentValues();

        for (GameTheme gameTheme : mWordThemeDataXmlLoader.getGameThemes()) {
            vals.clear();
            vals.put(DbContract.Theme.COL_NAME, gameTheme.getName());
            long gtid = db.insert(DbContract.Theme.TABLE_NAME, "null", vals);
            gameTheme.setId((int) gtid);
        }
    }

    private void prepopulateWordsData(SQLiteDatabase db) {
        ContentValues vals = new ContentValues();

        for (Word word : mWordThemeDataXmlLoader.getWords()) {
            vals.clear();
            vals.put(DbContract.WordBank.COL_GAME_THEME_ID, word.getGameThemeId());
            vals.put(DbContract.WordBank.COL_STRING, word.getString());
            vals.put(DbContract.WordBank.COL_SUB_STRING, word.getSubString());

            long wid = db.insert(DbContract.WordBank.TABLE_NAME, "null", vals);
            word.setId((int) wid);
        }
    }
}

