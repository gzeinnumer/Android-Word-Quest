package com.aar.app.wsp.features.gameplay

import com.aar.app.wsp.commons.Util
import com.aar.app.wsp.commons.generator.StringListGridGenerator
import com.aar.app.wsp.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

/**
 * Created by abdularis on 20/07/17.
 */
class GameDataCreator {

    fun newGameData(words: List<Word>,
                    rowCount: Int, colCount: Int,
                    name: String?,
                    gameMode: GameMode
    ): GameData {
        Util.randomizeList(words)
        val maxIndex = min(256, words.size)
        val grid = Grid(rowCount, colCount)
        val usedWords = StringListGridGenerator().setGrid(words.subList(0, maxIndex), grid.array)
        val gameName = if (name.isNullOrEmpty()) "Puzzle ${getDate()}" else name

        return GameData(
            name = gameName,
            gameMode = gameMode,
            grid = grid
        ).apply {
            addUsedWords(buildUsedWordFromWord(usedWords))
        }
    }

    private fun buildUsedWordFromWord(words: List<Word>): List<UsedWord> {
        val usedWords: MutableList<UsedWord> = ArrayList()
        for (i in words.indices) {
            val word = words[i]
            val uw = UsedWord()
            uw.gameThemeId = word.gameThemeId
            uw.string = word.string
            usedWords.add(uw)
        }
        Util.randomizeList(usedWords)
        return usedWords
    }

    private fun getDate(): String {
        return SimpleDateFormat("HH.mm.ss", Locale.ENGLISH)
            .format(Date(System.currentTimeMillis()))
    }
}