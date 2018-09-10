package com.aar.app.wordsearch.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by abdularis on 08/07/17.
 */

@Entity(tableName = "words")
public class Word {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;
    @ColumnInfo(name = "game_theme_id")
    private int mGameThemeId;
    @ColumnInfo(name = "string")
    private String mString;

    public Word() {
        this(0, "");
    }

    public Word(int id, String string) {
        mId = id;
        mString = string;
    }

    public Word(int id, int gameThemeId, String string) {
        mId = id;
        mGameThemeId = gameThemeId;
        mString = string;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getString() {
        return mString;
    }

    public void setString(String string) {
        mString = string;
    }

    public int getGameThemeId() {
        return mGameThemeId;
    }

    public void setGameThemeId(int gameThemeId) {
        mGameThemeId = gameThemeId;
    }

}
