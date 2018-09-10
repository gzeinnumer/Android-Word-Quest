package com.aar.app.wordsearch.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aar.app.wordsearch.commons.generator.StringGridGenerator;
import com.aar.app.wordsearch.data.room.UsedWordDataSource;
import com.aar.app.wordsearch.model.GameData;
import com.aar.app.wordsearch.model.GameDataInfo;
import com.aar.app.wordsearch.model.Grid;
import com.aar.app.wordsearch.model.UsedWord;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by abdularis on 18/07/17.
 */

public class GameDataSource {

    private DbHelper mHelper;
    private UsedWordDataSource mUsedWordDataSource;

    @Inject
    public GameDataSource(DbHelper helper, UsedWordDataSource usedWordDataSource) {
        mHelper = helper;
        mUsedWordDataSource = usedWordDataSource;
    }

    public GameData getGameData(int gid) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.GameRound._ID,
                DbContract.GameRound.COL_NAME,
                DbContract.GameRound.COL_DURATION,
                DbContract.GameRound.COL_GRID_ROW_COUNT,
                DbContract.GameRound.COL_GRID_COL_COUNT,
                DbContract.GameRound.COL_GRID_DATA
        };
        String sel = DbContract.GameRound._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        Cursor c = db.query(DbContract.GameRound.TABLE_NAME, cols, sel, selArgs, null, null, null);
        GameData gd = null;
        if (c.moveToFirst()) {
            gd = new GameData();
            gd.setId(c.getInt(0));
            gd.setName(c.getString(1));
            gd.setDuration(c.getInt(2));

            Grid grid = new Grid(c.getInt(3), c.getInt(4));
            String gridData = c.getString(5);
            if (gridData != null && gridData.length() > 0) {
                new StringGridGenerator().setGrid(gridData, grid.getArray());
            }

            gd.setGrid(grid);
            gd.addUsedWords(mUsedWordDataSource.getUsedWords(gid));
        }
        c.close();

        return gd;
    }

    public List<GameDataInfo> getGameDataInfos() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<GameDataInfo> infoList = new ArrayList<>();
        Cursor c = db.rawQuery(getGameDataInfoQuery(-1), null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                infoList.add(getGameDataInfoFromCursor(c));
                c.moveToNext();
            }
        }
        c.close();

        return infoList;
    }

    public GameDataInfo getGameDataInfo(int gid) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c = db.rawQuery(getGameDataInfoQuery(gid), null);
        GameDataInfo gameData = null;
        if (c.moveToFirst()) {
            gameData = getGameDataInfoFromCursor(c);
        }
        c.close();
        return gameData;
    }

    public long saveGameData(GameData gameRound) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.GameRound.COL_NAME, gameRound.getName());
        values.put(DbContract.GameRound.COL_DURATION, gameRound.getDuration());
        values.put(DbContract.GameRound.COL_GRID_ROW_COUNT, gameRound.getGrid().getRowCount());
        values.put(DbContract.GameRound.COL_GRID_COL_COUNT, gameRound.getGrid().getColCount());
        values.put(DbContract.GameRound.COL_GRID_DATA, gameRound.getGrid().toString());

        long gid = db.insert(DbContract.GameRound.TABLE_NAME, "null", values);
        gameRound.setId((int) gid);

        for (UsedWord usedWord : gameRound.getUsedWords())
            usedWord.setGameDataId((int) gid);
        mUsedWordDataSource.insertAll(gameRound.getUsedWords());
        gameRound.setUsedWords(mUsedWordDataSource.getUsedWords((int) gid));

        return gid;
    }

    public void deleteGameData(int gid) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sel = DbContract.GameRound._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        db.delete(DbContract.GameRound.TABLE_NAME, sel, selArgs);
        mUsedWordDataSource.removeUsedWords(gid);
    }

    public void deleteGameDatas() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(DbContract.GameRound.TABLE_NAME, null, null);
        mUsedWordDataSource.removeAll();
    }

    public void saveGameDataDuration(int gid, int newDuration) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.GameRound.COL_DURATION, newDuration);

        String where = DbContract.GameRound._ID + "=?";
        String whereArgs[] = {String.valueOf(gid)};

        db.update(DbContract.GameRound.TABLE_NAME, values, where, whereArgs);
    }

    public void markWordAsAnswered(UsedWord usedWord) {
        mUsedWordDataSource.updateUsedWord(usedWord);
    }

    private String getGameDataInfoQuery(int gid) {
        String order = " ORDER BY " + DbContract.GameRound._ID + " DESC";
        if (gid > 0) {
            order = " WHERE " + DbContract.GameRound._ID + "=" + gid;
        }

        return "SELECT " +
                DbContract.GameRound._ID + "," +
                DbContract.GameRound.COL_NAME + "," +
                DbContract.GameRound.COL_DURATION + "," +
                DbContract.GameRound.COL_GRID_ROW_COUNT + "," +
                DbContract.GameRound.COL_GRID_COL_COUNT +
                " FROM " + DbContract.GameRound.TABLE_NAME + order;
    }

    private GameDataInfo getGameDataInfoFromCursor(Cursor c) {
        GameDataInfo gdi = new GameDataInfo();
        gdi.setId(c.getInt(0));
        gdi.setName(c.getString(1));
        gdi.setDuration(c.getInt(2));
        gdi.setGridRowCount(c.getInt(3));
        gdi.setGridColCount(c.getInt(4));
        gdi.setUsedWordsCount(mUsedWordDataSource.getUsedWordsCount(gdi.getId()));
        return gdi;
    }
}
