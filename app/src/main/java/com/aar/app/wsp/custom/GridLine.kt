package com.aar.app.wsp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.aar.app.wsp.R

/**
 * Created by abdularis on 22/06/17.
 *
 * Digunakan untuk merender grid line
 * ____________
 * |   |   |   |
 * |---|---|---|
 * |___|___|___|
 * |   |   |   |
 */
class GridLine @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GridBehavior(context, attrs) {

    private var _lineWidth = 2
    private var _colCount = 8
    private var _rowCount = 8
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.GridLine, 0, 0)
            _lineWidth = a.getDimensionPixelSize(R.styleable.GridLine_lineWidth, _lineWidth)
            paint.color = a.getColor(R.styleable.GridLine_lineColor, Color.GRAY)
            _colCount = a.getInteger(R.styleable.GridLine_gridColumnCount, _colCount)
            _rowCount = a.getInteger(R.styleable.GridLine_gridRowCount, _rowCount)
            a.recycle()
        }
    }

    override fun getColCount(): Int {
        return _colCount
    }

    override fun getRowCount(): Int {
        return _rowCount
    }

    var lineWidth: Int
        get() = _lineWidth
        set(lineWidth) {
            _lineWidth = lineWidth
            invalidate()
        }

    var lineColor: Int
        get() = paint.color
        set(color) {
            paint.color = color
            invalidate()
        }

    override val requiredWidth: Int
        get() = super.requiredWidth + _lineWidth

    override val requiredHeight: Int
        get() = super.requiredHeight + _lineWidth

    override fun getCenterColFromIndex(cIdx: Int): Int {
        return super.getCenterColFromIndex(cIdx) + _lineWidth / 2
    }

    override fun getCenterRowFromIndex(rIdx: Int): Int {
        return super.getCenterRowFromIndex(rIdx) + _lineWidth / 2
    }

    override fun setColCount(colCount: Int) {
        _colCount = colCount
        invalidate()
        requestLayout()
    }

    override fun setRowCount(rowCount: Int) {
        _rowCount = rowCount
        invalidate()
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        val viewWidth = requiredWidth
        val viewHeight = requiredHeight
        val pLeft = paddingLeft
        val pTop = paddingTop
        val pRight = paddingRight
        val pBottom = paddingBottom
        var y = pTop + gridHeight.toFloat()
        // horizontal lines
        for (i in 1 until getRowCount()) {
            canvas.drawRect(pLeft.toFloat(), y, viewWidth + _lineWidth - pRight.toFloat(), y + _lineWidth, paint)
            y += gridHeight.toFloat()
        }
        var x = pLeft + gridWidth.toFloat()
        // vertical lines
        for (i in 1 until getColCount()) {
            canvas.drawRect(x, pTop.toFloat(), x + _lineWidth, viewHeight + _lineWidth - pBottom.toFloat(), paint)
            x += gridWidth.toFloat()
        }
    }
}