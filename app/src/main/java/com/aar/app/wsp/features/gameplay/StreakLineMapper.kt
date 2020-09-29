package com.aar.app.wsp.features.gameplay

import com.aar.app.wsp.commons.Mapper
import com.aar.app.wsp.custom.StreakView.StreakLine
import com.aar.app.wsp.model.UsedWord.AnswerLine

/**
 * Created by abdularis on 09/07/17.
 */
class StreakLineMapper : Mapper<AnswerLine, StreakLine>() {
    override fun map(obj: AnswerLine): StreakLine {
        return StreakLine().apply {
            startIndex[obj.startRow] = obj.startCol
            endIndex[obj.endRow] = obj.endCol
            color = obj.color
        }
    }

    override fun revMap(obj: StreakLine): AnswerLine {
        return AnswerLine(
            startRow = obj.startIndex.row,
            startCol = obj.startIndex.col,
            endRow = obj.endIndex.row,
            endCol = obj.endIndex.col,
            color = obj.color
        )
    }
}