package com.aar.app.wsp.features.gamehistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aar.app.wsp.commons.addTo
import com.aar.app.wsp.data.sqlite.GameDataSource
import com.aar.app.wsp.features.settings.Preferences
import com.aar.app.wsp.model.GameDataInfo
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GameHistoryViewModel(
    private val gameDataSource: GameDataSource,
    private val preferences: Preferences
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _onGameDataInfoLoaded: MutableLiveData<List<GameDataInfo>> = MutableLiveData()
    val onGameDataInfoLoaded: LiveData<List<GameDataInfo>>
        get() = _onGameDataInfoLoaded

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun loadGameHistory() {
        Observable
            .create(ObservableOnSubscribe { e: ObservableEmitter<List<GameDataInfo>> ->
                e.onNext(gameDataSource.gameDataInfos)
                e.onComplete()
            } as ObservableOnSubscribe<List<GameDataInfo>>)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { gameDataInfoList ->
                _onGameDataInfoLoaded.value = gameDataInfoList
                if (gameDataInfoList.isEmpty()) {
                    preferences.resetSaveGameDataCount()
                }
            }
            .addTo(disposables)
    }

    fun deleteGameData(gameDataInfo: GameDataInfo) {
        Completable
            .create { e: CompletableEmitter ->
                gameDataSource.deleteGameData(gameDataInfo.id)
                e.onComplete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { loadGameHistory() }
            .addTo(disposables)
    }

    fun clear() {
        Completable
            .create { e: CompletableEmitter ->
                gameDataSource.deleteGameDatas()
                e.onComplete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _onGameDataInfoLoaded.value = ArrayList()
                preferences.resetSaveGameDataCount()
            }
            .addTo(disposables)
    }
}