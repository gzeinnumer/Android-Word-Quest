package com.aar.app.wordsearch.features.gamehistory;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.aar.app.wordsearch.data.sqlite.GameDataSource;
import com.aar.app.wordsearch.model.GameDataInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class GameHistoryViewModel extends ViewModel {

    private GameDataSource mGameDataSource;
    private MutableLiveData<List<GameDataInfo>> mOnGameDataInfoLoaded;

    public GameHistoryViewModel(GameDataSource gameDataSource) {
        mGameDataSource = gameDataSource;
        mOnGameDataInfoLoaded = new MutableLiveData<>();
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
                .subscribe(mOnGameDataInfoLoaded::setValue);
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
                .subscribe(() -> mOnGameDataInfoLoaded.setValue(new ArrayList<>()));
    }

    public LiveData<List<GameDataInfo>> getOnGameDataInfoLoaded() {
        return mOnGameDataInfoLoaded;
    }
}
