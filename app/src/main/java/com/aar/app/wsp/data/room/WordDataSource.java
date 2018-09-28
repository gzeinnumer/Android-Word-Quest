package com.aar.app.wsp.data.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.aar.app.wsp.model.Word;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface WordDataSource {

    @Query("SELECT * FROM words WHERE LENGTH(string) < :maxChar")
    Flowable<List<Word>> getWords(int maxChar);

    @Query("SELECT * FROM words WHERE game_theme_id=:themeId AND LENGTH(string) < :maxChar")
    Flowable<List<Word>> getWords(int themeId, int maxChar);

    @Query("SELECT count(*) FROM words WHERE length(string) < :maxChar")
    Single<Integer> getWordsCount(int maxChar);

    @Query("SELECT count(*) FROM words WHERE game_theme_id=:themeId AND length(string) < :maxChar")
    Single<Integer> getWordsCount(int themeId, int maxChar);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Word> words);

    @Query("DELETE FROM words")
    void deleteAll();

}
