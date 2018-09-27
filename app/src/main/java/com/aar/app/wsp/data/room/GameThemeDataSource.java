package com.aar.app.wsp.data.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.aar.app.wsp.features.gamethemeselector.GameThemeItem;
import com.aar.app.wsp.model.GameTheme;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface GameThemeDataSource {

    @Query("SELECT * FROM game_themes")
    Flowable<List<GameTheme>> getThemes();

    @Query("SELECT *, (SELECT COUNT(*) FROM words WHERE game_theme_id=game_themes.id) as words_count FROM game_themes")
    Flowable<List<GameThemeItem>> getThemesItem();

    @Insert
    void insertAll(List<GameTheme> gameThemes);

    @Query("DELETE FROM game_themes")
    void deleteAll();

}
