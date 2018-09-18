package com.aar.app.wordsearch.data.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.aar.app.wordsearch.model.UsedWord;

import java.util.List;

@Dao
public interface UsedWordDataSource {

    @Query("SELECT * FROM used_words WHERE game_data_id=:gameDataId")
    List<UsedWord> getUsedWords(int gameDataId);

    @Query("SELECT COUNT(*) FROM used_words WHERE game_data_id=:gameDataId")
    int getUsedWordsCount(int gameDataId);

    @Query("UPDATE used_words SET answer_line=null, duration=0 WHERE game_data_id=:gameDataId")
    void resetUsedWords(int gameDataId);

    @Query("UPDATE used_words SET duration=:duration WHERE id=:usedWordId")
    void updateUsedWordDuration(int usedWordId, int duration);

    @Update
    void updateUsedWord(UsedWord usedWord);

    @Insert
    void insertAll(List<UsedWord> usedWords);

    @Query("DELETE FROM used_words WHERE game_data_id=:gameDataId")
    void removeUsedWords(int gameDataId);

    @Query("DELETE FROM used_words")
    void removeAll();

}
