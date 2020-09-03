package com.aar.app.wsp.model

import kotlin.math.max


/**
 * Created by abdularis on 08/07/17.
 */
class GameData @JvmOverloads constructor(
    var id: Int = 0,
    var name: String = "",
    var duration: Int = 0,
    var gameMode: GameMode = GameMode.Normal,
    var grid: Grid? = null,
    private val _usedWords: MutableList<UsedWord> = ArrayList()
) {

    var maxDuration = 0
    var usedWords: List<UsedWord>
        get() = _usedWords
        set(usedWords) {
            _usedWords.clear()
            _usedWords.addAll(usedWords)
        }

    val answeredWordsCount: Int
        get() {
            var count = 0
            for (uw in _usedWords) {
                if (uw.isAnswered) count++
            }
            return count
        }

    val isFinished: Boolean
        get() = answeredWordsCount == _usedWords.size

    val isGameOver: Boolean
        get() {
            if (gameMode === GameMode.CountDown) {
                return duration >= maxDuration
            } else if (gameMode === GameMode.Marathon) {
                for (usedWord in _usedWords) {
                    if (usedWord.isTimeout) return true
                }
            }
            return false
        }

    val remainingDuration: Int
        get() = max(0, maxDuration - duration)

    fun addUsedWords(usedWords: List<UsedWord>) {
        _usedWords.addAll(usedWords)
    }
}