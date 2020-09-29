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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by abdularis on 18/07/17.
 */
class GameDataSource @Inject constructor(private val dbHelper: DbHelper, private val usedWordDataSource: UsedWordDataSource) {
    suspend fun getGameData(gid: Int): GameData? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
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
        db.query(DbContract.GameRound.TABLE_NAME, cols, sel, selArgs, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val gameData = GameData()
                gameData.id = cursor.getInt(0)
                gameData.name = cursor.getString(1)
                gameData.duration = cursor.getInt(2)
                val grid = Grid(cursor.getInt(3), cursor.getInt(4))
                val gridData = cursor.getString(5)
                if (gridData != null && gridData.isNotEmpty()) {
                    StringGridGenerator().setGrid(gridData, grid.array)
                }
                gameData.grid = grid
                gameData.gameMode = getById(cursor.getInt(6))
                gameData.maxDuration = cursor.getInt(7)
                gameData.addUsedWords(usedWordDataSource.getUsedWords(gid))
                return@use gameData
            }
            return@use null
        }
    }

    fun getGameDataSync(gid: Int): GameData? {
        return runBlocking {
            getGameData(gid)
        }
    }

    val gameDataInfos: List<GameDataInfo>
        get() {
            val db = dbHelper.readableDatabase
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

    suspend fun getGameDataInfoList(): List<GameDataInfo> = withContext(Dispatchers.IO) {
        val infoList = arrayListOf<GameDataInfo>()
        dbHelper.readableDatabase.rawQuery(getGameDataInfoQuery(-1), null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    infoList.add(getGameDataInfoFromCursor(cursor))
                    cursor.moveToNext()
                }
            }
        }
        infoList
    }

    fun getGameDataInfo(gid: Int): GameDataInfo? {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(getGameDataInfoQuery(gid), null)
        var gameData: GameDataInfo? = null
        if (c.moveToFirst()) {
            gameData = getGameDataInfoFromCursor(c)
        }
        c.close()
        return gameData
    }

    fun saveGameData(gameRound: GameData): Long {
        val db = dbHelper.writableDatabase
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
        usedWordDataSource.insertAll(gameRound.usedWords)
        gameRound.usedWords = usedWordDataSource.getUsedWords(gid.toInt())
        return gid
    }

    suspend fun deleteGameData(gid: Int) = withContext(Dispatchers.IO) {
        val sel = DbContract.GameRound._ID + "=?"
        val selArgs = arrayOf(gid.toString())
        dbHelper.writableDatabase.delete(DbContract.GameRound.TABLE_NAME, sel, selArgs)
        usedWordDataSource.removeUsedWords(gid)
    }

    fun deleteGameDatas() {
        val db = dbHelper.writableDatabase
        db.delete(DbContract.GameRound.TABLE_NAME, null, null)
        usedWordDataSource.removeAll()
    }

    suspend fun clearGameData() = withContext(Dispatchers.IO) {
        dbHelper.writableDatabase.delete(DbContract.GameRound.TABLE_NAME, null, null)
        usedWordDataSource.removeAll()
    }

    fun saveGameDataDuration(gid: Int, newDuration: Int) {
        val db = dbHelper.readableDatabase
        val values = ContentValues()
        values.put(DbContract.GameRound.COL_DURATION, newDuration)
        val where = DbContract.GameRound._ID + "=?"
        val whereArgs = arrayOf(gid.toString())
        db.update(DbContract.GameRound.TABLE_NAME, values, where, whereArgs)
    }

    fun markWordAsAnswered(usedWord: UsedWord) {
        usedWordDataSource.updateUsedWord(usedWord)
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
        gdi.usedWordsCount = usedWordDataSource.getUsedWordsCount(gdi.id)
        return gdi
    }

}