package com.aar.app.wordsearch.data.network.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WordsUpdateResponse {

    @SerializedName("update")
    private boolean mUpdate;
    @SerializedName("revision")
    private int mRevision;
    @SerializedName("data")
    private List<ThemeResponse> mData;

    public boolean isUpdate() {
        return mUpdate;
    }

    public void setUpdate(boolean update) {
        mUpdate = update;
    }

    public int getRevision() {
        return mRevision;
    }

    public void setRevision(int revision) {
        mRevision = revision;
    }

    public List<ThemeResponse> getData() {
        return mData;
    }

    public void setData(List<ThemeResponse> data) {
        mData = data;
    }
}
