package com.aar.app.wsp.data.network.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThemeResponse {
    @SerializedName("theme")
    private String mName;
    @SerializedName("words")
    private List<String> mWords;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getWords() {
        return mWords;
    }

    public void setWords(List<String> words) {
        mWords = words;
    }
}
