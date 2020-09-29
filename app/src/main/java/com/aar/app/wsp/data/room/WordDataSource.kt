package com.aar.app.wsp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aar.app.wsp.model.Word
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface WordDataSource {
    @Query("SELECT * FROM words WHERE LENGTH(string) < :maxChar")
    fun getWords(maxChar: Int): Flowable<List<Word>>

    @Query("SELECT * FROM words WHERE game_theme_id=:themeId AND LENGTH(string) < :maxChar")
    fun getWords(themeId: Int, maxChar: Int): Flowable<List<Word>>

    @Query("SELECT count(*) FROM words WHERE length(string) < :maxChar")
    fun getWordsCount(maxChar: Int): Single<Int>

    @Query("SELECT count(*) FROM words WHERE game_theme_id=:themeId AND length(string) < :maxChar")
    fun getWordsCount(themeId: Int, maxChar: Int): Single<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(words: List<Word>)

    @Query("DELETE FROM words")
    fun deleteAll()
}