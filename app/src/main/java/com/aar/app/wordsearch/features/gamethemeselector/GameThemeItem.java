package com.aar.app.wordsearch.features.gamethemeselector;

import android.arch.persistence.room.ColumnInfo;

public class GameThemeItem {
    @ColumnInfo(name = "id")
    private int mId;
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "words_count")
    private int mWordsCount;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getWordsCount() {
        return mWordsCount;
    }

    public void setWordsCount(int wordsCount) {
        mWordsCount = wordsCount;
    }
}
