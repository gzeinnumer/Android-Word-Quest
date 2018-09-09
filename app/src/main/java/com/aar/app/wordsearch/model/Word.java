package com.aar.app.wordsearch.model;

/**
 * Created by abdularis on 08/07/17.
 */

public class Word {

    private int mId;
    private int mGameThemeId;
    private String mString;

    public Word() {
        this(-1, "");
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
