package com.aar.app.wsp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdularis on 08/07/17.
 */

public class GameData {

    private int mId;
    private String mName;
    private int mDuration;
    private GameMode mGameMode;
    private int mMaxDuration;
    private Grid mGrid;
    private List<UsedWord> mUsedWords;

    public GameData() {
        this(0, "", 0, GameMode.Normal, null, new ArrayList<>());
    }

    public GameData(int id, String name, int duration, GameMode gameMode, Grid grid, List<UsedWord> usedWords) {
        mId = id;
        mName = name;
        mDuration = duration;
        mGameMode = gameMode;
        mGrid = grid;
        mUsedWords = usedWords;
        mMaxDuration = 0;
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

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public GameMode getGameMode() {
        return mGameMode;
    }

    public void setGameMode(GameMode gameMode) {
        mGameMode = gameMode;
    }

    public Grid getGrid() {
        return mGrid;
    }

    public void setGrid(Grid grid) {
        mGrid = grid;
    }

    public int getMaxDuration() {
        return mMaxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        mMaxDuration = maxDuration;
    }

    public List<UsedWord> getUsedWords() {
        return mUsedWords;
    }

    public int getAnsweredWordsCount() {
        int count = 0;
        for (UsedWord uw : mUsedWords) {
            if (uw.isAnswered()) count++;
        }
        return count;
    }

    public boolean isFinished() {
        return getAnsweredWordsCount() == mUsedWords.size();
    }

    public boolean isGameOver() {
        if (mGameMode == GameMode.CountDown) {
            return mDuration >= mMaxDuration;
        } else if (mGameMode == GameMode.Marathon) {
            for (UsedWord usedWord : mUsedWords) {
                if (usedWord.isTimeout()) return true;
            }
        }
        return false;
    }

    public int getRemainingDuration() {
        return Math.max(0, mMaxDuration - mDuration);
    }

    public void setUsedWords(List<UsedWord> usedWords) {
        mUsedWords.clear();
        mUsedWords.addAll(usedWords);
    }

    public void addUsedWords(List<UsedWord> usedWords) {
        mUsedWords.addAll(usedWords);
    }
}
