package com.aar.app.wsp.features.gamethemeselector;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;

import com.aar.app.wsp.data.network.responses.ThemeResponse;
import com.aar.app.wsp.data.room.GameThemeDataSource;
import com.aar.app.wsp.data.network.RetrofitClient;
import com.aar.app.wsp.data.network.responses.WordsUpdateResponse;
import com.aar.app.wsp.data.room.WordDataSource;
import com.aar.app.wsp.model.GameTheme;
import com.aar.app.wsp.model.Word;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ThemeSelectorViewModel extends AndroidViewModel {

    private static final String PREF_NAME = "DataRev";
    private static final String KEY_DATA_REVISION = "data_revision";

    public enum ResponseType {
        NoUpdate,
        Updated
    }

    private SharedPreferences mPrefs;
    private GameThemeDataSource mGameThemeRepository;
    private WordDataSource mWordDataSource;

    private MutableLiveData<List<GameThemeItem>> mOnGameThemeLoaded;

    public ThemeSelectorViewModel(Application application,
                                  GameThemeDataSource gameThemeRepository,
                                  WordDataSource wordDataSource) {
        super(application);
        mGameThemeRepository = gameThemeRepository;
        mWordDataSource = wordDataSource;
        mOnGameThemeLoaded = new MutableLiveData<>();
        mPrefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @SuppressLint("CheckResult")
    public void loadThemes() {
        mGameThemeRepository.getThemesItem()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mOnGameThemeLoaded::setValue);
    }

    public Observable<ResponseType> updateData() {
        return RetrofitClient.getInstance().getWordDataService()
                .fetchWordsData(getLastDataRevision())
                .doOnNext(wordsUpdateResponse -> {
                    if (!wordsUpdateResponse.isUpdate()) return;

                    List<GameTheme> themes = new ArrayList<>();
                    List<Word> words = new ArrayList<>();

                    int idx = 0;
                    for (ThemeResponse themeResponse : wordsUpdateResponse.getData()) {
                        themes.add(new GameTheme(++idx, themeResponse.getName()));

                        for (String str : themeResponse.getWords()) {
                            words.add(new Word(0, idx, str));
                        }
                    }

                    mGameThemeRepository.deleteAll();
                    mWordDataSource.deleteAll();
                    mGameThemeRepository.insertAll(themes);
                    mWordDataSource.insertAll(words);
                    setLastDataRevision(wordsUpdateResponse.getRevision());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<WordsUpdateResponse, Observable<ResponseType>>) wordsUpdateResponse -> {
                    if (wordsUpdateResponse.isUpdate())
                        return Observable.just(ResponseType.Updated);
                    return Observable.just(ResponseType.NoUpdate);
                });
    }

    public Single<Boolean> checkWordAvailability(int themeId, int rowCount, int colCount) {
        int maxChar = Math.max(rowCount, colCount);
        Single<Integer> singleSource;
        if (themeId == GameTheme.NONE.getId())
            singleSource = mWordDataSource.getWordsCount(maxChar);
        else
            singleSource = mWordDataSource.getWordsCount(themeId, maxChar);

        return singleSource
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<Integer, Single<Boolean>>) count -> Single.just(count > 0));
    }

    public LiveData<List<GameThemeItem>> getOnGameThemeLoaded() {
        return mOnGameThemeLoaded;
    }

    public int getLastDataRevision() {
        return mPrefs.getInt(KEY_DATA_REVISION, 0);
    }

    private void setLastDataRevision(int revision) {
        mPrefs.edit()
                .putInt(KEY_DATA_REVISION, revision)
                .apply();
    }
}
