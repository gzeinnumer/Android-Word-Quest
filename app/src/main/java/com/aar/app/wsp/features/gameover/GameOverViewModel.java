package com.aar.app.wsp.features.gameover;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.aar.app.wsp.commons.SingleLiveEvent;
import com.aar.app.wsp.data.room.UsedWordDataSource;
import com.aar.app.wsp.data.sqlite.GameDataSource;
import com.aar.app.wsp.model.GameData;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GameOverViewModel extends ViewModel {

    private GameDataSource mGameDataSource;
    private UsedWordDataSource mUsedWordDataSource;
    private GameData mGameData;
    private MutableLiveData<GameData> mOnGameDataLoaded = new MutableLiveData<>();
    private SingleLiveEvent<Integer> mOnGameDataReset = new SingleLiveEvent<>();

    public GameOverViewModel(GameDataSource gameDataSource, UsedWordDataSource usedWordDataSource) {
        mGameDataSource = gameDataSource;
        mUsedWordDataSource = usedWordDataSource;
    }

    @SuppressLint("CheckResult")
    public void loadData(int gid) {
        Observable
                .create((ObservableOnSubscribe<GameData>) e -> {
                    mGameData = mGameDataSource.getGameData(gid);
                    e.onNext(mGameData);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mOnGameDataLoaded::setValue);
    }

    public void deleteGameRound() {
        if (mGameData == null) return;
        Completable
                .create(e -> {
                    mGameDataSource.deleteGameData(mGameData.getId());
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    public void resetCurrentGameData() {
        if (mGameData != null) {
            Completable
                    .create(e -> {
                        mUsedWordDataSource.resetUsedWords(mGameData.getId());
                        mGameDataSource.saveGameDataDuration(mGameData.getId(), 0);
                        e.onComplete();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mOnGameDataReset.setValue(mGameData.getId()));
        }
    }

    public LiveData<Integer> getOnGameDataReset() {
        return mOnGameDataReset;
    }

    public LiveData<GameData> getOnGameDataLoaded() {
        return mOnGameDataLoaded;
    }
}
