package com.aar.app.wsp.custom

import java.util.*

/**
 * Created by abdularis on 26/06/17.
 */
abstract class LetterGridDataAdapter : Observable() {
    abstract fun getRowCount(): Int
    abstract fun getColCount(): Int
    abstract fun getLetter(row: Int, col: Int): Char
}