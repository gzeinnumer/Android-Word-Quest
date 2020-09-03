package com.aar.app.wsp.data.room;

import androidx.room.TypeConverter;

import com.aar.app.wsp.model.UsedWord;

public final class AnswerLineConverter {

    @TypeConverter
    public static String answerLineToString(UsedWord.AnswerLine answerLine) {
        return answerLine == null ? null : answerLine.toString();
    }

    @TypeConverter
    public static UsedWord.AnswerLine stringToAnswerLine(String answerLineData) {
        if (answerLineData == null) return null;
        UsedWord.AnswerLine answerLine = new UsedWord.AnswerLine();
        answerLine.fromString(answerLineData);
        return answerLine;
    }
}
