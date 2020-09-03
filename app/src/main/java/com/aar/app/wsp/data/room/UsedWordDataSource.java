package com.aar.app.wsp.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.aar.app.wsp.model.UsedWord;

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
