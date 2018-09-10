package com.aar.app.wordsearch.data.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.aar.app.wordsearch.model.GameTheme;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface GameThemeDataSource {

    @Query("SELECT * FROM game_themes")
    Flowable<List<GameTheme>> getThemes();

    @Insert
    void insertAll(List<GameTheme> gameThemes);

}
