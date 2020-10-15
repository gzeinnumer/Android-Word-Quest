package com.aar.app.wsp.commons

import android.graphics.Color
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by abdularis on 23/06/17.
 */
object Util {
    const val NULL_CHAR = '\u0000'
    private val sRand = Random()

    fun getRandomColorWithAlpha(alpha: Int): Int {
        val r = randomInt % 256
        val g = randomInt % 256
        val b = randomInt % 256
        return Color.argb(alpha, r, g, b)
    }

    // ASCII A = 65 - Z = 90
    private val randomChar: Char
        get() =// ASCII A = 65 - Z = 90
            getRandomIntRange(65, 90).toChar()

    /**
     * generate random integer between min and max (inclusive)
     * example: min = 5, max = 7 output would be (5, 6, 7)
     *
     * @param min minimum integer number to be generated
     * @param max maximum integer number to be generated (inclusive)
     * @return integer between min - max
     */
    private fun getRandomIntRange(min: Int, max: Int): Int {
        return min + randomInt % (max - min + 1)
    }

    @JvmStatic
    val randomInt: Int
        get() = abs(sRand.nextInt())

    @JvmStatic
    fun getIndexLength(start: GridIndex, end: GridIndex): Int {
        val x = abs(start.col - end.col)
        val y = abs(start.row - end.row)
        return max(x, y) + 1
    }

    fun <T> randomizeList(list: MutableList<T>) {
        val count = list.size
        for (i in 0 until count) {
            val randIdx = getRandomIntRange(min(i + 1, count - 1), count - 1)
            val temp = list[randIdx]
            list[randIdx] = list[i]
            list[i] = temp
        }
    }

    fun getReverseString(str: String): String {
        val out = StringBuilder()
        for (i in str.length - 1 downTo 0) out.append(str[i])
        return out.toString()
    }

    /**
     * Isi slot / element yang masih kosong dengan karakter acak
     *
     */
    @JvmStatic
    fun fillNullCharWidthRandom(gridArr: Array<CharArray>) {
        for (i in gridArr.indices) {
            for (j in gridArr[i].indices) {
                if (gridArr[i][j] == NULL_CHAR) gridArr[i][j] = randomChar
            }
        }
    }
}