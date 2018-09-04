package com.aar.app.wordsearch.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aar.app.wordsearch.commons.generator.StringGridGenerator;
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

    @Inject
    public GameDataSource(DbHelper helper) {
        mHelper = helper;
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
            gd.addUsedWords(getUsedWords(gid));
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

        for (UsedWord usedWord : gameRound.getUsedWords()) {
            values.clear();
            values.put(DbContract.UsedWord.COL_GAME_ROUND_ID, gid);
            values.put(DbContract.UsedWord.COL_WORD_STRING, usedWord.getString());
            values.put(DbContract.UsedWord.COL_IS_MYSTERY, usedWord.isMystery() ? "true" : "false");
            values.put(DbContract.UsedWord.COL_REVEAL_COUNT, usedWord.getRevealCount());
            if (usedWord.getAnswerLine() != null) {
                values.put(DbContract.UsedWord.COL_ANSWER_LINE_DATA, usedWord.getAnswerLine().toString());
                values.put(DbContract.UsedWord.COL_LINE_COLOR, usedWord.getAnswerLine().color);
            }

            long insertedId = db.insert(DbContract.UsedWord.TABLE_NAME, "null", values);
            usedWord.setId((int) insertedId);
        }

        return gid;
    }

    public void deleteGameData(int gid) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sel = DbContract.GameRound._ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        db.delete(DbContract.GameRound.TABLE_NAME, sel, selArgs);

        sel = DbContract.UsedWord.COL_GAME_ROUND_ID + "=?";
        db.delete(DbContract.UsedWord.TABLE_NAME, sel, selArgs);
    }

    public void deleteGameDatas() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(DbContract.GameRound.TABLE_NAME, null, null);
        db.delete(DbContract.UsedWord.TABLE_NAME, null, null);
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
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.UsedWord.COL_ANSWER_LINE_DATA, usedWord.getAnswerLine().toString());
        values.put(DbContract.UsedWord.COL_LINE_COLOR, usedWord.getAnswerLine().color);

        String where = DbContract.UsedWord._ID + "=?";
        String whereArgs[] = {String.valueOf(usedWord.getId())};

        db.update(DbContract.UsedWord.TABLE_NAME, values, where, whereArgs);
    }

    private String getGameDataInfoQuery(int gid) {
        String subQ = "(SELECT COUNT(*) FROM " + DbContract.UsedWord.TABLE_NAME + " WHERE " +
                DbContract.UsedWord.COL_GAME_ROUND_ID + "=" + DbContract.GameRound.TABLE_NAME + "." + DbContract.GameRound._ID + ")";
        String order = " ORDER BY " + DbContract.GameRound._ID + " DESC";
        if (gid > 0) {
            subQ = "(SELECT COUNT(*) FROM " + DbContract.UsedWord.TABLE_NAME + " WHERE " +
                    DbContract.UsedWord.COL_GAME_ROUND_ID + "=" + gid + ")";
            order = " WHERE " + DbContract.UsedWord._ID + "=" + gid;
        }

        return "SELECT " +
                DbContract.GameRound._ID + "," +
                DbContract.GameRound.COL_NAME + "," +
                DbContract.GameRound.COL_DURATION + "," +
                DbContract.GameRound.COL_GRID_ROW_COUNT + "," +
                DbContract.GameRound.COL_GRID_COL_COUNT + "," +
                subQ +
                " FROM " + DbContract.GameRound.TABLE_NAME + order;
    }

    private GameDataInfo getGameDataInfoFromCursor(Cursor c) {
        GameDataInfo gdi = new GameDataInfo();
        gdi.setId(c.getInt(0));
        gdi.setName(c.getString(1));
        gdi.setDuration(c.getInt(2));
        gdi.setGridRowCount(c.getInt(3));
        gdi.setGridColCount(c.getInt(4));
        gdi.setUsedWordsCount(c.getInt(5));
        return gdi;
    }

    private List<UsedWord> getUsedWords(int gid) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.UsedWord._ID,
                DbContract.UsedWord.COL_WORD_STRING,
                DbContract.UsedWord.COL_ANSWER_LINE_DATA,
                DbContract.UsedWord.COL_LINE_COLOR,
                DbContract.UsedWord.COL_IS_MYSTERY,
                DbContract.UsedWord.COL_REVEAL_COUNT
        };
        String sel = DbContract.UsedWord.COL_GAME_ROUND_ID + "=?";
        String selArgs[] = {String.valueOf(gid)};

        Cursor c = db.query(DbContract.UsedWord.TABLE_NAME, cols, sel, selArgs, null, null, null);

        List<UsedWord> usedWordList = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                int id = c.getInt(0);
                String str = c.getString(1);
                String lineData = c.getString(2);
                int col = c.getInt(3);

                UsedWord.AnswerLine answerLine = null;
                if (lineData != null) {
                    answerLine = new UsedWord.AnswerLine();
                    answerLine.fromString(lineData);
                    answerLine.color = col;
                }

                UsedWord usedWord = new UsedWord();
                usedWord.setId(id);
                usedWord.setString(str);
                usedWord.setAnswered(lineData != null);
                usedWord.setAnswerLine(answerLine);
                usedWord.setIsMystery(Boolean.valueOf(c.getString(4)));
                usedWord.setRevealCount(c.getInt(5));

                usedWordList.add(usedWord);

                c.moveToNext();
            }
        }
        c.close();

        return usedWordList;
    }
}
