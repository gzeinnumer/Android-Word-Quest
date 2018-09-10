package com.aar.app.wordsearch.features.gameplay;


import com.aar.app.wordsearch.commons.Util;
import com.aar.app.wordsearch.commons.generator.StringListGridGenerator;
import com.aar.app.wordsearch.model.GameData;
import com.aar.app.wordsearch.model.GameMode;
import com.aar.app.wordsearch.model.Grid;
import com.aar.app.wordsearch.model.UsedWord;
import com.aar.app.wordsearch.model.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by abdularis on 20/07/17.
 */

public class GameDataCreator {

    public GameData newGameData(final List<Word> words,
                                final int rowCount, final int colCount,
                                final String name,
                                final GameMode gameMode) {
        final GameData gameData = new GameData();
        gameData.setGameMode(gameMode);

        Util.randomizeList(words);

        Grid grid = new Grid(rowCount, colCount);
        int maxCharCount = Math.min(rowCount, colCount);
        List<Word> usedWords =
                new StringListGridGenerator()
                        .setGrid(filterWordList(words, 100, maxCharCount), grid.getArray());

        gameData.addUsedWords(buildUsedWordFromWord(usedWords));
        gameData.setGrid(grid);
        if (name == null || name.isEmpty()) {
            String name1 = "Puzzle " +
                    new SimpleDateFormat("HH.mm.ss", Locale.ENGLISH)
                            .format(new Date(System.currentTimeMillis()));
            gameData.setName(name1);
        }
        else {
            gameData.setName(name);
        }
        return gameData;
    }

    private List<UsedWord> buildUsedWordFromWord(List<Word> words) {
        List<UsedWord> usedWords = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);

            UsedWord uw = new UsedWord();
            uw.setGameThemeId(word.getGameThemeId());
            uw.setString(word.getString());
            uw.setAnswered(false);

            usedWords.add(uw);
        }

        Util.randomizeList(usedWords);
        return usedWords;
    }

    private List<Word> filterWordList(List<Word> words, int count, int maxCharCount) {
        count = Math.min(count, words.size());

        List<Word> wordList = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (wordList.size() >= count) break;

            if (words.get(i).getString().length() <= maxCharCount) {
                wordList.add(words.get(i));
            }
        }

        return wordList;
    }
}
