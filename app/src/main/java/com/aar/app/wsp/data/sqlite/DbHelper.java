package com.aar.app.wsp.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abdularis on 18/07/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_TABLE_GAME_ROUND =
            "CREATE TABLE " + DbContract.GameRound.TABLE_NAME + " (" +
                    DbContract.GameRound._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbContract.GameRound.COL_NAME + " TEXT," +
                    DbContract.GameRound.COL_DURATION + " INTEGER," +
                    DbContract.GameRound.COL_GRID_ROW_COUNT + " INTEGER," +
                    DbContract.GameRound.COL_GRID_COL_COUNT + " INTEGER," +
                    DbContract.GameRound.COL_GRID_DATA + " TEXT," +
                    DbContract.GameRound.COL_GAME_MODE + " INTEGER," +
                    DbContract.GameRound.COL_MAX_DURATION + " INTEGER)";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_GAME_ROUND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

