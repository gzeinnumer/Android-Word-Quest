package com.aar.app.wsp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.aar.app.wsp.data.room.AnswerLineConverter
import kotlin.math.max

/**
 * Created by abdularis on 08/07/17.
 */
@Entity(tableName = "used_words")
class UsedWord : Word() {
    @ColumnInfo(name = "game_data_id")
    var gameDataId = 0

    @TypeConverters(AnswerLineConverter::class)
    @ColumnInfo(name = "answer_line")
    var answerLine: AnswerLine? = null

    @ColumnInfo(name = "duration")
    var duration = 0

    @ColumnInfo(name = "max_duration")
    var maxDuration = 0

    val isAnswered: Boolean
        get() = answerLine != null

    val isTimeout: Boolean
        get() = duration >= maxDuration

    val remainingDuration: Int
        get() = max(0, maxDuration - duration)

    class AnswerLine @JvmOverloads constructor(
        @JvmField var startRow: Int = 0,
        @JvmField var startCol: Int = 0,
        @JvmField var endRow: Int = 0,
        @JvmField var endCol: Int = 0,
        @JvmField var color: Int = 0
    ) {
        override fun toString(): String {
            return "$startRow,$startCol:$endRow,$endCol:$color"
        }

        /**
         * Expected format string = startRow,startCol:endRow,endCol:color
         * example: 1,1:6,6:1538382300
         */
        fun fromString(string: String?) {
            if (string == null) return
            val split = string.split(":".toRegex(), 3)
            if (split.size < 3) return

            val start = split[0].split(",".toRegex(), 2)
            val end = split[1].split(",".toRegex(), 2)
            color = split[2].toInt()
            if (start.size >= 2 && end.size >= 2) {
                startRow = start[0].toInt()
                startCol = start[1].toInt()
                endRow = end[0].toInt()
                endCol = end[1].toInt()
            }
        }
    }
}