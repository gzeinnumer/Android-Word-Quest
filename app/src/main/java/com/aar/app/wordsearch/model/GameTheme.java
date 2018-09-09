package com.aar.app.wordsearch.model;

public class GameTheme {

    public static final GameTheme NONE = new GameTheme(-1, "");

    private int mId;
    private String mName;

    public GameTheme(int id, String name) {
        mId = id;
        mName = name;
    }

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
}
