package com.aar.app.wsp.features.gameplay;


import com.aar.app.wsp.commons.Util;
import com.aar.app.wsp.commons.generator.StringListGridGenerator;
import com.aar.app.wsp.model.GameData;
import com.aar.app.wsp.model.GameMode;
import com.aar.app.wsp.model.Grid;
import com.aar.app.wsp.model.UsedWord;
import com.aar.app.wsp.model.Word;

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

        int maxIndex = Math.min(256, words.size() - 1);
        Grid grid = new Grid(rowCount, colCount);
        List<Word> usedWords = new StringListGridGenerator().setGrid(words.subList(0, maxIndex), grid.getArray());

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

            usedWords.add(uw);
        }

        Util.randomizeList(usedWords);
        return usedWords;
    }

}
