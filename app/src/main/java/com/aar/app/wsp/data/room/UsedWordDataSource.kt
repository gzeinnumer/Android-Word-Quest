package com.aar.app.wsp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.aar.app.wsp.model.UsedWord

@Dao
interface UsedWordDataSource {
    @Query("SELECT * FROM used_words WHERE game_data_id=:gameDataId")
    fun getUsedWords(gameDataId: Int): List<UsedWord>

    @Query("SELECT COUNT(*) FROM used_words WHERE game_data_id=:gameDataId")
    fun getUsedWordsCount(gameDataId: Int): Int

    @Query("UPDATE used_words SET answer_line=null, duration=0 WHERE game_data_id=:gameDataId")
    fun resetUsedWords(gameDataId: Int)

    @Query("UPDATE used_words SET duration=:duration WHERE id=:usedWordId")
    fun updateUsedWordDuration(usedWordId: Int, duration: Int)

    @Update
    fun updateUsedWord(usedWord: UsedWord)

    @Insert
    fun insertAll(usedWords: List<UsedWord>)

    @Query("DELETE FROM used_words WHERE game_data_id=:gameDataId")
    fun removeUsedWords(gameDataId: Int)

    @Query("DELETE FROM used_words")
    fun removeAll()
}