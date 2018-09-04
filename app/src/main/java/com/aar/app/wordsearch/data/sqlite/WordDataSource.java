package com.aar.app.wordsearch.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aar.app.wordsearch.model.Word;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by abdularis on 18/07/17.
 */

public class WordDataSource {

    private DbHelper mHelper;

    @Inject
    public WordDataSource(DbHelper helper) {
        mHelper = helper;
    }

    public List<Word> getWords() {
        return getWords(-1);
    }

    public List<Word> getWords(int gameThemeId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String cols[] = {
                DbContract.WordBank._ID,
                DbContract.WordBank.COL_GAME_THEME_ID,
                DbContract.WordBank.COL_STRING,
                DbContract.WordBank.COL_SUB_STRING
        };

        Cursor c;
        if (gameThemeId <= -1) {
            c = db.query(DbContract.WordBank.TABLE_NAME, cols, null, null, null, null, null);
        } else {
            String sel = DbContract.WordBank.COL_GAME_THEME_ID + "=?";
            String selArgs[] = {String.valueOf(gameThemeId)};
            c = db.query(DbContract.WordBank.TABLE_NAME, cols, sel, selArgs, null, null, null);
        }

        List<Word> wordList = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                Word word = new Word(
                        c.getInt(0),
                        c.getInt(1),
                        c.getString(2),
                        c.getString(3));
                wordList.add(word);

                c.moveToNext();
            }
        }

        c.close();
        return wordList;
    }

//    public void insertAll(List<Word> words) {
//        SQLiteDatabase db = mHelper.getWritableDatabase();
//        ContentValues vals = new ContentValues();
//
//        for (Word word : words) {
//            vals.clear();
//            vals.put(DbContract.WordBank.COL_GAME_THEME_ID, word.getGameThemeId());
//            vals.put(DbContract.WordBank.COL_STRING, word.getString());
//            vals.put(DbContract.WordBank.COL_SUB_STRING, word.getSubString());
//
//            long wid = db.insert(DbContract.WordBank.TABLE_NAME, "null", vals);
//            word.setId((int) wid);
//        }
//    }

}
