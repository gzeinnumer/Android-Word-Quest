package com.aar.app.wsp.features.gameplay;

import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aar.app.wsp.commons.SingleLiveEvent;
import com.aar.app.wsp.commons.Timer;
import com.aar.app.wsp.commons.Util;
import com.aar.app.wsp.data.room.UsedWordDataSource;
import com.aar.app.wsp.data.room.WordDataSource;
import com.aar.app.wsp.data.sqlite.GameDataSource;
import com.aar.app.wsp.features.settings.Preferences;
import com.aar.app.wsp.model.GameData;
import com.aar.app.wsp.model.Difficulty;
import com.aar.app.wsp.model.GameMode;
import com.aar.app.wsp.model.GameTheme;
import com.aar.app.wsp.model.UsedWord;
import com.aar.app.wsp.model.Word;

import java.util.List;

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
        boolean win;
        GameData mGameData;
        private Finished(GameData gameData, boolean win) {
            this.mGameData = gameData;
            this.win = win;
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
    private UsedWordDataSource mUsedWordDataSource;
    private Preferences mPreferences;
    private GameDataCreator mGameDataCreator;
    private GameData mCurrentGameData;
    private Timer mTimer;
    private int mCurrentDuration;
    private UsedWord mCurrentUsedWord;

    private GameState mCurrentState = null;
    private MutableLiveData<Integer> mOnTimer;
    private MutableLiveData<Integer> mOnCountDown;
    private MutableLiveData<Integer> mOnCurrentWordCountDown;
    private MutableLiveData<GameState> mOnGameState;
    private SingleLiveEvent<AnswerResult> mOnAnswerResult;
    private MutableLiveData<UsedWord> mOnCurrentWordChanged;

    public GamePlayViewModel(GameDataSource gameDataSource,
                             WordDataSource wordDataSource,
                             UsedWordDataSource usedWordDataSource,
                             Preferences preferences) {
        mGameDataSource = gameDataSource;
        mWordDataSource = wordDataSource;
        mUsedWordDataSource = usedWordDataSource;
        mGameDataCreator = new GameDataCreator();
        mPreferences = preferences;

        mTimer = new Timer(TIMER_TIMEOUT);
        mTimer.addOnTimeoutListener(this::onTimerTimeout);
        resetLiveData();
    }

    public void stopGame() {
        mCurrentGameData = null;
        mTimer.stop();
        resetLiveData();
    }

    public void pauseGame() {
        if (mCurrentState instanceof Playing) {
            if (!mCurrentGameData.isFinished() && !mCurrentGameData.isGameOver()) {
                mTimer.stop();
                setGameState(new Paused());
            }
        }
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
                        startGame();
                    });
        }
    }

    @SuppressLint("CheckResult")
    public void generateNewGameRound(int rowCount, int colCount, int gameThemeId,
                                     GameMode gameMode, Difficulty difficulty) {
        if (!(mCurrentState instanceof Generating)) {
            String gameName = getGameDataName();
            setGameState(new Generating(rowCount, colCount, gameName));

            int maxChar = Math.max(rowCount, colCount);
            Flowable<List<Word>> flowableWords;
            if (gameThemeId == GameTheme.NONE.getId()) {
                flowableWords = mWordDataSource.getWords(maxChar);
            } else {
                flowableWords = mWordDataSource.getWords(gameThemeId, maxChar);
            }

            flowableWords.toObservable()
                    .flatMap((Function<List<Word>, Observable<List<Word>>>) words -> {
                        return Flowable.fromIterable(words)
                                .distinct(Word::getString)
                                .map(word -> {
                                    word.setString(word.getString().toUpperCase());
                                    return word;
                                })
                                .toList()
                                .toObservable();
                    })
                    .flatMap((Function<List<Word>, Observable<GameData>>) words -> {
                        GameData gameData = mGameDataCreator.newGameData(words, rowCount, colCount, gameName, gameMode);
                        if (gameMode == GameMode.CountDown) {
                            gameData.setMaxDuration(getMaxCountDownDuration(gameData.getUsedWords().size(), difficulty));
                        } else if (gameMode == GameMode.Marathon) {
                            int maxDuration = getMaxDurationPerWord(difficulty);
                            for (UsedWord usedWord : gameData.getUsedWords()) {
                                usedWord.setMaxDuration(maxDuration);
                            }
                        }
                        return Observable.just(gameData);
                    })
                    .doOnNext(mGameDataSource::saveGameData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gameData -> {
                        mCurrentDuration = 0;
                        mCurrentGameData = gameData;
                        startGame();
                    });
        }
    }

    public void answerWord(String answerStr, UsedWord.AnswerLine answerLine, boolean reverseMatching) {
        if (!(mCurrentState instanceof Playing)) return;

        UsedWord correctWord;
        if (mCurrentGameData.getGameMode() == GameMode.Marathon) {
            if (matchCurrentUsedWord(answerStr, reverseMatching)) {
                correctWord = mCurrentUsedWord;
            } else {
                correctWord = null;
            }
        } else {
            correctWord = findUsedWord(answerStr, reverseMatching);
        }

        boolean correct = false;
        if (correctWord != null) {
            correctWord.setAnswerLine(answerLine);
            correct = true;
        }
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
                mTimer.stop();
                finishGame(true);
            } else if (mCurrentGameData.getGameMode() == GameMode.Marathon) {
                nextWord();
            }
        }
    }

    public LiveData<Integer> getOnTimer() {
        return mOnTimer;
    }

    public LiveData<Integer> getOnCountDown() {
        return mOnCountDown;
    }

    public LiveData<GameState> getOnGameState() {
        return mOnGameState;
    }

    public LiveData<AnswerResult> getOnAnswerResult() {
        return mOnAnswerResult;
    }

    public LiveData<UsedWord> getOnCurrentWordChanged() {
        return mOnCurrentWordChanged;
    }

    public LiveData<Integer> getOnCurrentWordCountDown() {
        return mOnCurrentWordCountDown;
    }

    private void startGame() {
        setGameState(new Playing(mCurrentGameData));
        if (!mCurrentGameData.isFinished() && !mCurrentGameData.isGameOver()) {
            if (mCurrentGameData.getGameMode() == GameMode.Marathon)
                nextWord();
            mTimer.start();
        }
    }

    private void setGameState(GameState state) {
        mCurrentState = state;
        mOnGameState.setValue(mCurrentState);
    }

    private String getGameDataName() {
        String num = String.valueOf(mPreferences.getPreviouslySavedGameDataCount());
        mPreferences.incrementSavedGameDataCount();
        return "Puzzle - " + num;
    }

    private int getMaxCountDownDuration(int usedWordsCount, Difficulty difficulty) {
        if (difficulty == Difficulty.Easy) {
            return usedWordsCount * 19; // 19s per word
        } else if (difficulty == Difficulty.Medium) {
            return usedWordsCount * 10; // 10s per word
        } else {
            return usedWordsCount * 5; // 5s per word
        }
    }

    private int getMaxDurationPerWord(Difficulty difficulty) {
        if (difficulty == Difficulty.Easy) {
            return 25; // 19s per word
        } else if (difficulty == Difficulty.Medium) {
            return 16; // 10s per word
        } else {
            return 10; // 5s per word
        }
    }

    private void nextWord() {
        if (mCurrentGameData != null && mCurrentGameData.getGameMode() == GameMode.Marathon) {
            mCurrentUsedWord = null;
            for (UsedWord usedWord : mCurrentGameData.getUsedWords()) {
                if (!usedWord.isAnswered() && !usedWord.isTimeout()) {
                    mCurrentUsedWord = usedWord;
                    break;
                }
            }

            if (mCurrentUsedWord != null) {
                mTimer.stop();
                mTimer.start();
                mOnCurrentWordChanged.setValue(mCurrentUsedWord);
            }
        }
    }

    private void finishGame(boolean win) {
        setGameState(new Finished(mCurrentGameData, win));
    }

    private UsedWord findUsedWord(String word, boolean enableReverse) {
        String answerStrRev = Util.getReverseString(word);
        for (UsedWord usedWord : mCurrentGameData.getUsedWords()) {

            if (usedWord.isAnswered()) continue;

            String currUsedWord = usedWord.getString();
            if (currUsedWord.equalsIgnoreCase(word) ||
                    (currUsedWord.equalsIgnoreCase( answerStrRev ) && enableReverse)) {
                return usedWord;
            }
        }
        return null;
    }

    private boolean matchCurrentUsedWord(String word, boolean enableReverse) {
        if (mCurrentUsedWord == null) return false;
        String answerStrRev = Util.getReverseString(word);
        String currUsedWord = mCurrentUsedWord.getString();
        return currUsedWord.equalsIgnoreCase(word) ||
                (currUsedWord.equalsIgnoreCase(answerStrRev) && enableReverse);
    }

    private void onTimerTimeout(long elapsedTime) {
        if (mCurrentGameData != null && mTimer.isStarted()) {
            mCurrentGameData.setDuration(++mCurrentDuration);

            GameMode gameMode = mCurrentGameData.getGameMode();
            if (gameMode == GameMode.CountDown) {
                mOnCountDown.setValue(mCurrentGameData.getRemainingDuration());
                if (mCurrentGameData.getRemainingDuration() <= 0) {
                    boolean win = mCurrentGameData.getAnsweredWordsCount() ==
                            mCurrentGameData.getUsedWords().size();
                    mTimer.stop();
                    finishGame(win);
                }
            } else if (gameMode == GameMode.Marathon && mCurrentUsedWord != null) {
                mCurrentUsedWord.setDuration(mCurrentUsedWord.getDuration() + 1);
                mOnCurrentWordCountDown.setValue(mCurrentUsedWord.getMaxDuration() - mCurrentUsedWord.getDuration());
                Completable
                        .create(e -> {
                            mUsedWordDataSource.updateUsedWordDuration(
                                    mCurrentUsedWord.getId(),
                                    mCurrentUsedWord.getDuration());
                            e.onComplete();
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                if (mCurrentUsedWord.isTimeout()) {
                    mTimer.stop();
                    finishGame(false);
                }
            }

            mOnTimer.setValue(mCurrentDuration);
            mGameDataSource.saveGameDataDuration(mCurrentGameData.getId(), mCurrentDuration);
        }
    }

    private void resetLiveData() {
        mOnTimer = new MutableLiveData<>();
        mOnCountDown = new MutableLiveData<>();
        mOnGameState = new MutableLiveData<>();
        mOnAnswerResult = new SingleLiveEvent<>();
        mOnCurrentWordChanged = new MutableLiveData<>();
        mOnCurrentWordCountDown = new MutableLiveData<>();
    }
}
