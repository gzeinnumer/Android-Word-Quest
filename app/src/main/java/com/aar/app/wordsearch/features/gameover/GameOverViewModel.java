package com.aar.app.wordsearch.features.gameover;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.aar.app.wordsearch.data.sqlite.GameDataSource;
import com.aar.app.wordsearch.model.GameDataInfo;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GameOverViewModel extends ViewModel {

    private GameDataSource mGameDataSource;
    private MutableLiveData<GameDataInfo> mOnGameDataInfoLoaded = new MutableLiveData<>();

    public GameOverViewModel(GameDataSource gameDataSource) {
        mGameDataSource = gameDataSource;
    }

    @SuppressLint("CheckResult")
    public void loadData(int gid) {
        Observable
                .create((ObservableOnSubscribe<GameDataInfo>) e -> {
                    e.onNext(mGameDataSource.getGameDataInfo(gid));
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

    public LiveData<GameDataInfo> getOnGameDataInfoLoaded() {
        return mOnGameDataInfoLoaded;
    }
}
