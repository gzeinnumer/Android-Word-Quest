package com.aar.app.wordsearch.features.gameplay;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.aar.app.wordsearch.commons.SingleLiveEvent;
import com.aar.app.wordsearch.commons.Timer;
import com.aar.app.wordsearch.data.room.WordDataSource;
import com.aar.app.wordsearch.data.sqlite.GameDataSource;
import com.aar.app.wordsearch.model.GameData;
import com.aar.app.wordsearch.model.GameMode;
import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.model.UsedWord;
import com.aar.app.wordsearch.model.Word;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class GamePlayViewModel extends ViewModel {

    private static final int TIMER_TIMEOUT = 1000;

    static abstract class GameState {}
    static class Generating extends GameState {
        int rowCount;
        int colCount;
        String name;
        private Generating(int rowCount, int colCount, String name) {
            this.rowCount = rowCount;
            this.colCount = colCount;
            this.name = name;
        }
    }
    static class Loading extends GameState {
        int gid;
        private Loading(int gid) {
            this.gid = gid;
        }
    }
    static class Finished extends GameState {
        GameData mGameData;
        private Finished(GameData gameData) {
            this.mGameData = gameData;
        }
    }
    static class Paused extends GameState {
        private Paused() {}
    }
    static class Playing extends GameState {
        GameData mGameData;
        private Playing(GameData gameData) {
            this.mGameData = gameData;
        }
    }

    static class AnswerResult {
        public boolean correct;
        public UsedWord usedWord;
        public int totalAnsweredWord;
        AnswerResult(boolean correct, UsedWord usedWord, int totalAnsweredWord) {
            this.correct = correct;
            this.usedWord = usedWord;
            this.totalAnsweredWord = totalAnsweredWord;
        }
    }

    private GameDataSource mGameDataSource;
    private WordDataSource mWordDataSource;
    private GameDataCreator mGameDataCreator;
    private GameData mCurrentGameData;
    private Timer mTimer;
    private int mCurrentDuration;

    private GameState mCurrentState = null;
    private MutableLiveData<Integer> mOnTimer;
    private MutableLiveData<GameState> mOnGameState;
    private SingleLiveEvent<AnswerResult> mOnAnswerResult;

    public GamePlayViewModel(GameDataSource gameDataSource, WordDataSource wordDataSource) {
        mGameDataSource = gameDataSource;
        mWordDataSource = wordDataSource;
        mGameDataCreator = new GameDataCreator();

        mTimer = new Timer(TIMER_TIMEOUT);
        mTimer.addOnTimeoutListener(elapsedTime -> {
            if (mCurrentGameData != null) {
                mOnTimer.setValue(mCurrentDuration++);
                mGameDataSource.saveGameDataDuration(mCurrentGameData.getId(), mCurrentDuration);
            }
        });
        resetLiveData();
    }

    private void resetLiveData() {
        mOnTimer = new MutableLiveData<>();
        mOnGameState = new MutableLiveData<>();
        mOnAnswerResult = new SingleLiveEvent<>();
    }

    public void stopGame() {
        mCurrentGameData = null;
        mTimer.stop();
        resetLiveData();
    }

    public void pauseGame() {
        mTimer.stop();
        setGameState(new Paused());
    }

    public void resumeGame() {
        if (mCurrentState instanceof Paused) {
            mTimer.start();
            setGameState(new Playing(mCurrentGameData));
        }
    }

    @SuppressLint("CheckResult")
    public void loadGameRound(int gid) {
        if (!(mCurrentState instanceof Generating)) {
            setGameState(new Loading(gid));
            Observable
                    .create((ObservableOnSubscribe<GameData>) e -> {
                        e.onNext(mGameDataSource.getGameData(gid));
                        e.onComplete();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gameData -> {
                        mCurrentGameData = gameData;
                        mCurrentDuration = mCurrentGameData.getDuration();
                        if (!mCurrentGameData.isFinished())
                            mTimer.start();
                        setGameState(new Playing(mCurrentGameData));
                    });
        }
    }

    @SuppressLint("CheckResult")
    public void generateNewGameRound(int rowCount, int colCount, int gameThemeId, GameMode gameMode) {
        if (!(mCurrentState instanceof Generating)) {
            String gameName = getGameDataName();
            setGameState(new Generating(rowCount, colCount, gameName));

            Flowable<List<Word>> flowableWords;
            if (gameThemeId == GameTheme.NONE.getId()) {
                flowableWords = mWordDataSource.getWords();
            } else {
                flowableWords = mWordDataSource.getWords(gameThemeId);
            }

            flowableWords.toObservable()
                    .flatMap((Function<List<Word>, Observable<GameData>>) words -> Observable.just(
                            mGameDataCreator.newGameData(words, rowCount, colCount, gameName, gameMode)
                    ))
                    .doOnNext(mGameDataSource::saveGameData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gameRound -> {
                        mCurrentDuration = 0;
                        mCurrentGameData = gameRound;
                        if (!mCurrentGameData.isFinished())
                            mTimer.start();
                        setGameState(new Playing(mCurrentGameData));
                    });
        }
    }

    public void answerWord(String answerStr, UsedWord.AnswerLine answerLine, boolean reverseMatching) {
        UsedWord correctWord = mCurrentGameData.markWordAsAnswered(answerStr, answerLine, reverseMatching);

        boolean correct = correctWord != null;
        mOnAnswerResult.setValue(new AnswerResult(
                correct,
                correctWord,
                mCurrentGameData.getAnsweredWordsCount()
        ));
        if (correct) {
            Completable.create(e -> mGameDataSource.markWordAsAnswered(correctWord))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            if (mCurrentGameData.isFinished()) {
                setGameState(new Finished(mCurrentGameData));
            }
        }
    }

    public LiveData<Integer> getOnTimer() {
        return mOnTimer;
    }

    public LiveData<GameState> getOnGameState() {
        return mOnGameState;
    }

    public LiveData<AnswerResult> getOnAnswerResult() {
        return mOnAnswerResult;
    }

    private void setGameState(GameState state) {
        mCurrentState = state;
        mOnGameState.setValue(mCurrentState);
    }

    private String getGameDataName() {
        String date = new SimpleDateFormat("yyyy-M-d H:m:s", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
        return "Puzzle - " + date;
    }
}
