package com.aar.app.wordsearch.features.mainmenu;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.aar.app.wordsearch.data.sqlite.GameThemeDataSource;
import com.aar.app.wordsearch.model.GameTheme;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainMenuViewModel extends ViewModel {

    private GameThemeDataSource mGameThemeRepository;

    private List<GameTheme> mGameThemes;
    private MutableLiveData<List<String>> mOnGameThemeLoaded;

    public MainMenuViewModel(GameThemeDataSource gameThemeRepository) {
        mGameThemeRepository = gameThemeRepository;
        mOnGameThemeLoaded = new MutableLiveData<>();
    }

    @SuppressLint("CheckResult")
    public void loadThemes() {
        Observable.create((ObservableOnSubscribe<List<GameTheme>>) e -> {
            mGameThemes = mGameThemeRepository.getThemes();
            mGameThemes.add(0, new GameTheme(-1, "- All Theme -"));
            e.onNext(mGameThemes);
            e.onComplete();
        })
                .flatMap((Function<List<GameTheme>, Observable<GameTheme>>) Observable::fromIterable)
                .map(GameTheme::getName)
                .toList()
                .toObservable()
                .subscribe(mOnGameThemeLoaded::setValue);
    }

    public int getGameThemeIdByIndex(int index) {
        if (mGameThemes == null || index < 0 || index >= mGameThemes.size()) {
            return -1;
        }
        return mGameThemes.get(index).getId();
    }

    public LiveData<List<String>> getOnGameThemeLoaded() {
        return mOnGameThemeLoaded;
    }
}
