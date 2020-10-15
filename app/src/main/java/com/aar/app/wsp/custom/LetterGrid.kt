package com.aar.app.wsp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import com.aar.app.wsp.R
import com.aar.app.wsp.commons.orZero
import java.util.*

/**
 * Created by abdularis on 22/06/17.
 *
 * Render grid of letters
 */
class LetterGrid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GridBehavior(context, attrs), Observer {

    init {
        init(context, attrs)
    }

    private lateinit var paint: Paint
    private lateinit var charBounds: Rect
    private var gridDataAdapter: LetterGridDataAdapter? = null

    var letterSize: Float
        get() = paint.textSize
        set(letterSize) {
            paint.textSize = letterSize
            invalidate()
        }

    var letterColor: Int
        get() = paint.color
        set(color) {
            paint.color = color
            invalidate()
        }

    var dataAdapter: LetterGridDataAdapter?
        get() = gridDataAdapter
        set(newDataAdapter) {
            requireNotNull(newDataAdapter) { "Data Adapater can't be null" }
            if (newDataAdapter !== gridDataAdapter) {
                gridDataAdapter?.deleteObserver(this)
                gridDataAdapter = newDataAdapter
                gridDataAdapter?.addObserver(this)
                invalidate()
                requestLayout()
            }
        }

    override fun getColCount(): Int {
        return gridDataAdapter?.getColCount().orZero()
    }

    override fun getRowCount(): Int {
        return gridDataAdapter?.getRowCount().orZero()
    }

    override fun setColCount(colCount: Int) {
        // do nothing
    }

    override fun setRowCount(rowCount: Int) {
        // do nothing
    }

    override fun update(o: Observable, arg: Any) {
        invalidate()
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        val gridColCount = getColCount()
        val gridRowCount = getRowCount()
        val halfWidth = gridWidth / 2
        val halfHeight = gridHeight / 2
        var x: Int
        var y = halfHeight + paddingTop

        // iterate and render all letters found in grid data adapter
        for (i in 0 until gridRowCount) {
            x = halfWidth + paddingLeft
            for (j in 0 until gridColCount) {
                val letter = gridDataAdapter?.getLetter(i, j)
                paint.getTextBounds(letter.toString(), 0, 1, charBounds)
                canvas.drawText(letter.toString(),
                    x - charBounds.exactCenterX(), y - charBounds.exactCenterY(), paint)
                x += gridWidth
            }
            y += gridHeight
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = 32.0f
        charBounds = Rect()
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LetterGrid, 0, 0)
            paint.textSize = a.getDimension(R.styleable.LetterGrid_letterSize, paint.textSize)
            paint.color = a.getColor(R.styleable.LetterGrid_letterColor, Color.GRAY)
            a.recycle()
        }
        dataAdapter = SampleLetterGridDataAdapter(8, 8)
    }
}