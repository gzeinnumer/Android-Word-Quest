package com.aar.app.wsp.data.room

import androidx.room.TypeConverter
import com.aar.app.wsp.model.UsedWord.AnswerLine

object AnswerLineConverter {
    @JvmStatic
    @TypeConverter
    fun answerLineToString(answerLine: AnswerLine?): String? {
        return answerLine?.toString()
    }

    @JvmStatic
    @TypeConverter
    fun stringToAnswerLine(answerLineData: String?): AnswerLine? {
        if (answerLineData == null) return null
        val answerLine = AnswerLine()
        answerLine.fromString(answerLineData)
        return answerLine
    }
}