package com.aar.app.wsp.features.gamehistory;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.aar.app.wsp.data.sqlite.GameDataSource;
import com.aar.app.wsp.features.settings.Preferences;
import com.aar.app.wsp.model.GameDataInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GameHistoryViewModel extends ViewModel {

    private GameDataSource mGameDataSource;
    private Preferences mPreferences;
    private MutableLiveData<List<GameDataInfo>> mOnGameDataInfoLoaded;

    public GameHistoryViewModel(GameDataSource gameDataSource, Preferences preferences) {
        mGameDataSource = gameDataSource;
        mOnGameDataInfoLoaded = new MutableLiveData<>();
        mPreferences = preferences;
    }

    @SuppressLint("CheckResult")
    public void loadGameHistory() {
        Observable
                .create((ObservableOnSubscribe<List<GameDataInfo>>) e -> {
                    e.onNext(mGameDataSource.getGameDataInfos());
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameDataInfos -> {
                    mOnGameDataInfoLoaded.setValue(gameDataInfos);
                    if (gameDataInfos.size() <= 0) {
                        mPreferences.resetSaveGameDataCount();
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void deleteGameData(GameDataInfo gameDataInfo) {
        Completable
                .create(e -> {
                    mGameDataSource.deleteGameData(gameDataInfo.getId());
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadGameHistory);
    }

    @SuppressLint("CheckResult")
    public void clear() {
        Completable
                .create(e -> {
                    mGameDataSource.deleteGameDatas();
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    mOnGameDataInfoLoaded.setValue(new ArrayList<>());
                    mPreferences.resetSaveGameDataCount();
                });
    }

    public LiveData<List<GameDataInfo>> getOnGameDataInfoLoaded() {
        return mOnGameDataInfoLoaded;
    }
}
