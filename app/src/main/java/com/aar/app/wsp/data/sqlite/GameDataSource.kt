package com.aar.app.wsp.data.sqlite

import android.content.ContentValues
import android.database.Cursor
import com.aar.app.wsp.commons.generator.StringGridGenerator
import com.aar.app.wsp.data.room.UsedWordDataSource
import com.aar.app.wsp.model.GameData
import com.aar.app.wsp.model.GameDataInfo
import com.aar.app.wsp.model.GameMode.Companion.getById
import com.aar.app.wsp.model.Grid
import com.aar.app.wsp.model.UsedWord
import java.util.*
import javax.inject.Inject

/**
 * Created by abdularis on 18/07/17.
 */
class GameDataSource @Inject constructor(private val mHelper: DbHelper, private val mUsedWordDataSource: UsedWordDataSource) {
    fun getGameData(gid: Int): GameData? {
        val db = mHelper.readableDatabase
        val cols = arrayOf(
            DbContract.GameRound._ID,
            DbContract.GameRound.COL_NAME,
            DbContract.GameRound.COL_DURATION,
            DbContract.GameRound.COL_GRID_ROW_COUNT,
            DbContract.GameRound.COL_GRID_COL_COUNT,
            DbContract.GameRound.COL_GRID_DATA,
            DbContract.GameRound.COL_GAME_MODE,
            DbContract.GameRound.COL_MAX_DURATION
        )
        val sel = DbContract.GameRound._ID + "=?"
        val selArgs = arrayOf(gid.toString())
        val c = db.query(DbContract.GameRound.TABLE_NAME, cols, sel, selArgs, null, null, null)
        var gd: GameData? = null
        if (c.moveToFirst()) {
            gd = GameData()
            gd.id = c.getInt(0)
            gd.name = c.getString(1)
            gd.duration = c.getInt(2)
            val grid = Grid(c.getInt(3), c.getInt(4))
            val gridData = c.getString(5)
            if (gridData != null && gridData.length > 0) {
                StringGridGenerator().setGrid(gridData, grid.array)
            }
            gd.grid = grid
            gd.gameMode = getById(c.getInt(6))
            gd.maxDuration = c.getInt(7)
            gd.addUsedWords(mUsedWordDataSource.getUsedWords(gid))
        }
        c.close()
        return gd
    }

    val gameDataInfos: List<GameDataInfo>
        get() {
            val db = mHelper.readableDatabase
            val infoList: MutableList<GameDataInfo> = ArrayList()
            val c = db.rawQuery(getGameDataInfoQuery(-1), null)
            if (c.moveToFirst()) {
                while (!c.isAfterLast) {
                    infoList.add(getGameDataInfoFromCursor(c))
                    c.moveToNext()
                }
            }
            c.close()
            return infoList
        }

    fun getGameDataInfo(gid: Int): GameDataInfo? {
        val db = mHelper.readableDatabase
        val c = db.rawQuery(getGameDataInfoQuery(gid), null)
        var gameData: GameDataInfo? = null
        if (c.moveToFirst()) {
            gameData = getGameDataInfoFromCursor(c)
        }
        c.close()
        return gameData
    }

    fun saveGameData(gameRound: GameData): Long {
        val db = mHelper.writableDatabase
        val values = ContentValues()
        values.put(DbContract.GameRound.COL_NAME, gameRound.name)
        values.put(DbContract.GameRound.COL_DURATION, gameRound.duration)
        values.put(DbContract.GameRound.COL_GRID_ROW_COUNT, gameRound.grid!!.rowCount)
        values.put(DbContract.GameRound.COL_GRID_COL_COUNT, gameRound.grid!!.colCount)
        values.put(DbContract.GameRound.COL_GRID_DATA, gameRound.grid.toString())
        values.put(DbContract.GameRound.COL_GAME_MODE, gameRound.gameMode.id)
        values.put(DbContract.GameRound.COL_MAX_DURATION, gameRound.maxDuration)
        val gid = db.insert(DbContract.GameRound.TABLE_NAME, "null", values)
        gameRound.id = gid.toInt()
        for (usedWord in gameRound.usedWords) usedWord.gameDataId = gid.toInt()
        mUsedWordDataSource.insertAll(gameRound.usedWords)
        gameRound.usedWords = mUsedWordDataSource.getUsedWords(gid.toInt())
        return gid
    }

    fun deleteGameData(gid: Int) {
        val db = mHelper.writableDatabase
        val sel = DbContract.GameRound._ID + "=?"
        val selArgs = arrayOf(gid.toString())
        db.delete(DbContract.GameRound.TABLE_NAME, sel, selArgs)
        mUsedWordDataSource.removeUsedWords(gid)
    }

    fun deleteGameDatas() {
        val db = mHelper.writableDatabase
        db.delete(DbContract.GameRound.TABLE_NAME, null, null)
        mUsedWordDataSource.removeAll()
    }

    fun saveGameDataDuration(gid: Int, newDuration: Int) {
        val db = mHelper.readableDatabase
        val values = ContentValues()
        values.put(DbContract.GameRound.COL_DURATION, newDuration)
        val where = DbContract.GameRound._ID + "=?"
        val whereArgs = arrayOf(gid.toString())
        db.update(DbContract.GameRound.TABLE_NAME, values, where, whereArgs)
    }

    fun markWordAsAnswered(usedWord: UsedWord?) {
        mUsedWordDataSource.updateUsedWord(usedWord)
    }

    private fun getGameDataInfoQuery(gid: Int): String {
        var order = " ORDER BY " + DbContract.GameRound._ID + " DESC"
        if (gid > 0) {
            order = " WHERE " + DbContract.GameRound._ID + "=" + gid
        }
        return "SELECT " +
            DbContract.GameRound._ID + "," +
            DbContract.GameRound.COL_NAME + "," +
            DbContract.GameRound.COL_DURATION + "," +
            DbContract.GameRound.COL_GRID_ROW_COUNT + "," +
            DbContract.GameRound.COL_GRID_COL_COUNT +
            " FROM " + DbContract.GameRound.TABLE_NAME + order
    }

    private fun getGameDataInfoFromCursor(c: Cursor): GameDataInfo {
        val gdi = GameDataInfo()
        gdi.id = c.getInt(0)
        gdi.name = c.getString(1)
        gdi.duration = c.getInt(2)
        gdi.gridRowCount = c.getInt(3)
        gdi.gridColCount = c.getInt(4)
        gdi.usedWordsCount = mUsedWordDataSource.getUsedWordsCount(gdi.id)
        return gdi
    }

}