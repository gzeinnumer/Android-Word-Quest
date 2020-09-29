package com.aar.app.wsp.custom

/**
 * Created by abdularis on 28/06/17.
 *
 * Sample data adapter (for preview in android studio visual editor)
 */
internal class SampleLetterGridDataAdapter(
    private val rowCount: Int,
    private val colCount: Int
) : LetterGridDataAdapter() {

    override fun getRowCount(): Int = rowCount

    override fun getColCount(): Int = colCount

    override fun getLetter(row: Int, col: Int): Char {
        return 'A'
    }

}