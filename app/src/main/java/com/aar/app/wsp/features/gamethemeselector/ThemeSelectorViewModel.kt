package com.aar.app.wsp.features.gamethemeselector

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.aar.app.wsp.data.network.RetrofitClient
import com.aar.app.wsp.data.network.responses.WordsUpdateResponse
import com.aar.app.wsp.data.room.GameThemeDataSource
import com.aar.app.wsp.data.room.WordDataSource
import com.aar.app.wsp.model.GameTheme
import com.aar.app.wsp.model.Word
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.math.max

class ThemeSelectorViewModel @Inject constructor(
    application: Application,
    private val gameThemeRepository: GameThemeDataSource,
    private val wordDataSource: WordDataSource
) : AndroidViewModel(application) {

    enum class ResponseType {
        NoUpdate, Updated
    }

    private val prefs: SharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val _onGameThemeLoaded: MediatorLiveData<List<GameThemeItem>> = MediatorLiveData()

    fun loadThemes() {
        _onGameThemeLoaded.addSource(gameThemeRepository.getThemeItemList()) {
            _onGameThemeLoaded.value = it
        }
    }

    fun updateData(): Observable<ResponseType> {
        return RetrofitClient.instance.wordDataService
            .fetchWordsData(lastDataRevision)
            .doOnNext { wordsUpdateResponse: WordsUpdateResponse ->
                if (!wordsUpdateResponse.isUpdate) return@doOnNext
                val themes: MutableList<GameTheme> = ArrayList()
                val words: MutableList<Word> = ArrayList()
                var idx = 0
                for (themeResponse in wordsUpdateResponse.data!!) {
                    themes.add(GameTheme(++idx, themeResponse.name))
                    for (str in themeResponse.words!!) {
                        words.add(Word(0, idx, str))
                    }
                }
                gameThemeRepository.deleteAll()
                wordDataSource.deleteAll()
                gameThemeRepository.insertAll(themes)
                wordDataSource.insertAll(words)
                lastDataRevision = wordsUpdateResponse.revision
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                if (it.isUpdate)
                    Observable.just(ResponseType.Updated)
                else
                    Observable.just(ResponseType.NoUpdate)
            }
    }

    fun checkWordAvailability(themeId: Int, rowCount: Int, colCount: Int): Single<Boolean> {
        val maxChar = max(rowCount, colCount)
        val singleSource: Single<Int>
        singleSource = if (themeId == GameTheme.NONE.id) wordDataSource.getWordsCount(maxChar) else wordDataSource.getWordsCount(themeId, maxChar)
        return singleSource
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(Function { count: Int -> Single.just(count > 0) } as Function<Int, Single<Boolean>>)
    }

    val onGameThemeLoaded: LiveData<List<GameThemeItem>>
        get() = _onGameThemeLoaded

    var lastDataRevision: Int
        get() = prefs.getInt(KEY_DATA_REVISION, 0)
        private set(revision) {
            prefs.edit()
                .putInt(KEY_DATA_REVISION, revision)
                .apply()
        }

    companion object {
        private const val PREF_NAME = "DataRev"
        private const val KEY_DATA_REVISION = "data_revision"
    }
}