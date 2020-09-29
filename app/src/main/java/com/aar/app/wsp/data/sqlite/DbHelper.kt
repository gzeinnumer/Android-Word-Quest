package com.aar.app.wsp.data.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by abdularis on 18/07/17.
 */
class DbHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_GAME_ROUND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        private const val DB_NAME = "data.db"
        private const val DB_VERSION = 1
        private const val SQL_CREATE_TABLE_GAME_ROUND = "CREATE TABLE " + DbContract.GameRound.TABLE_NAME + " (" +
            DbContract.GameRound._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DbContract.GameRound.COL_NAME + " TEXT," +
            DbContract.GameRound.COL_DURATION + " INTEGER," +
            DbContract.GameRound.COL_GRID_ROW_COUNT + " INTEGER," +
            DbContract.GameRound.COL_GRID_COL_COUNT + " INTEGER," +
            DbContract.GameRound.COL_GRID_DATA + " TEXT," +
            DbContract.GameRound.COL_GAME_MODE + " INTEGER," +
            DbContract.GameRound.COL_MAX_DURATION + " INTEGER)"
    }
}