package com.aar.app.wsp.features.gameover;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.aar.app.wsp.commons.SingleLiveEvent;
import com.aar.app.wsp.data.room.UsedWordDataSource;
import com.aar.app.wsp.data.sqlite.GameDataSource;
import com.aar.app.wsp.model.GameDataInfo;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GameOverViewModel extends ViewModel {

    private GameDataSource mGameDataSource;
    private UsedWordDataSource mUsedWordDataSource;
    private GameDataInfo mGameDataInfo;
    private MutableLiveData<GameDataInfo> mOnGameDataInfoLoaded = new MutableLiveData<>();
    private SingleLiveEvent<Integer> mOnGameDataReset = new SingleLiveEvent<>();

    public GameOverViewModel(GameDataSource gameDataSource, UsedWordDataSource usedWordDataSource) {
        mGameDataSource = gameDataSource;
        mUsedWordDataSource = usedWordDataSource;
    }

    @SuppressLint("CheckResult")
    public void loadData(int gid) {
        Observable
                .create((ObservableOnSubscribe<GameDataInfo>) e -> {
                    mGameDataInfo = mGameDataSource.getGameDataInfo(gid);
                    e.onNext(mGameDataInfo);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mOnGameDataInfoLoaded::setValue);
    }

    public void deleteGameRound(int gid) {
        Completable
                .create(e -> {
                    mGameDataSource.deleteGameData(gid);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    public void resetCurrentGameData() {
        if (mGameDataInfo != null) {
            Completable
                    .create(e -> {
                        mUsedWordDataSource.resetUsedWords(mGameDataInfo.getId());
                        mGameDataSource.saveGameDataDuration(mGameDataInfo.getId(), 0);
                        e.onComplete();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mOnGameDataReset.setValue(mGameDataInfo.getId()));
        }
    }

    public LiveData<Integer> getOnGameDataReset() {
        return mOnGameDataReset;
    }

    public LiveData<GameDataInfo> getOnGameDataInfoLoaded() {
        return mOnGameDataInfoLoaded;
    }
}
