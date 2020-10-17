package com.aar.app.wsp.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.aar.app.wsp.R
import com.aar.app.wsp.commons.Direction
import com.aar.app.wsp.commons.Direction.Companion.fromLine
import com.aar.app.wsp.commons.GridIndex
import com.aar.app.wsp.commons.Util.getIndexLength
import com.aar.app.wsp.custom.StreakView.OnInteractionListener
import com.aar.app.wsp.custom.StreakView.SnapType.Companion.fromId
import com.aar.app.wsp.custom.StreakView.StreakLine
import com.aar.app.wsp.custom.layout.CenterLayout
import java.util.*

/**
 * Created by abdularis on 26/06/17.
 *
 * Compound view untuk wsp game
 * yang memiliki tiga layer yaitu
 * - GridLine sebagai background
 * - StreakView sebagai middleground jadi akan dirender diatas background
 * dan dibawah foreground
 * - LetterGrid sebagai foreground yang menampilkan letters (huruf-huruf)
 * yang akan dirender paling atas
 */
class LetterBoard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CenterLayout(context, attrs), Observer {

    lateinit var gridLineBackground: GridLine
    lateinit var streakView: StreakView
    lateinit var letterGrid: LetterGrid
    private lateinit var _dataAdapter: LetterGridDataAdapter

    private var initialized = false
    var selectionListener: OnLetterSelectionListener? = null
        private set

    init {
        init(context, attrs)
    }

    override fun update(observable: Observable, arg: Any) {
        if (observable == _dataAdapter) {
            // ketika data grid berubah maka update row dan column count
            // dari grid line agar memiliki dimensi yang sama
            gridLineBackground.setColCount(_dataAdapter.getColCount())
            gridLineBackground.setRowCount(_dataAdapter.getRowCount())

            // ketika dimensi row dan column dari data grid berubah
            // maka harus di layout/dikalkulasikan kembali ukuran dari streak view
            streakView.invalidate()
            streakView.requestLayout()
        }
    }

    fun scale(scaleX: Float, scaleY: Float) {
        if (initialized) {
            gridLineBackground.gridWidth = (gridLineBackground.gridWidth * scaleX).toInt()
            gridLineBackground.gridHeight = (gridLineBackground.gridHeight * scaleY).toInt()
            // mGridLineBg.setLineWidth((int) (mGridLineBg.getLineWidth() * scaleX));
            letterGrid.gridWidth = (letterGrid.gridWidth * scaleX).toInt()
            letterGrid.gridHeight = (letterGrid.gridHeight * scaleY).toInt()
            letterGrid.letterSize = letterGrid.letterSize * scaleY
            streakView.streakWidth = (streakView.streakWidth * scaleY).toInt()

            // remove all views and re attach them, so this layout get re measure
            removeAllViews()
            attachAllViews()
            streakView.invalidateStreakLine()
        }
    }

    val gridColCount: Int
        get() = _dataAdapter.getColCount()
    val gridRowCount: Int
        get() = _dataAdapter.getRowCount()

    var dataAdapter: LetterGridDataAdapter
        get() = _dataAdapter
        set(dataAdapter) {
            if (dataAdapter != _dataAdapter) {
                _dataAdapter.deleteObserver(this)
                _dataAdapter = dataAdapter
                _dataAdapter.addObserver(this)
                letterGrid.dataAdapter = _dataAdapter
                gridLineBackground.setColCount(_dataAdapter.getColCount())
                gridLineBackground.setRowCount(_dataAdapter.getRowCount())
            }
        }

    fun addStreakLines(streakLines: List<StreakLine>) {
        streakView.addStreakLines(streakLines, false)
    }

    fun addStreakLine(streakLine: StreakLine?) {
        if (streakLine != null) streakView.addStreakLine(streakLine, true)
    }

    fun popStreakLine() {
        streakView.popStreakLine()
    }

    fun removeAllStreakLine() {
        streakView.removeAllStreakLine()
    }

    fun setGridWidth(width: Int) {
        gridLineBackground.gridWidth = width
        letterGrid.gridWidth = width
    }

    fun setGridHeight(height: Int) {
        gridLineBackground.gridHeight = height
        letterGrid.gridHeight = height
    }

    fun setGridLineVisibility(visible: Boolean) {
        if (!visible) gridLineBackground.visibility = INVISIBLE else gridLineBackground.visibility = VISIBLE
    }

    fun setGridLineColor(color: Int) {
        gridLineBackground.lineColor = color
    }

    fun setGridLineWidth(width: Int) {
        gridLineBackground.lineWidth = width
    }

    fun setLetterSize(size: Float) {
        letterGrid.letterSize = size
    }

    fun setLetterColor(color: Int) {
        letterGrid.letterColor = color
    }

    fun setStreakWidth(width: Int) {
        streakView.streakWidth = width
    }

    fun setOnLetterSelectionListener(listener: OnLetterSelectionListener?) {
        selectionListener = listener
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        gridLineBackground = GridLine(context)
        streakView = StreakView(context)
        letterGrid = LetterGrid(context)
        var gridWidth = 50
        var gridHeight = 50
        var gridColCount = 8
        var gridRowCount = 8
        var lineColor = Color.GRAY
        var lineWidth = 2
        var letterSize = 32.0f
        var letterColor = Color.GRAY
        var streakWidth = 35
        var snapToGrid = 0
        var gridLineVisibility = true
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LetterBoard, 0, 0)
            gridWidth = a.getDimensionPixelSize(R.styleable.LetterBoard_gridWidth, gridWidth)
            gridHeight = a.getDimensionPixelSize(R.styleable.LetterBoard_gridHeight, gridHeight)
            gridColCount = a.getInteger(R.styleable.LetterBoard_gridColumnCount, gridColCount)
            gridRowCount = a.getInteger(R.styleable.LetterBoard_gridRowCount, gridRowCount)
            lineColor = a.getColor(R.styleable.LetterBoard_lineColor, lineColor)
            lineWidth = a.getDimensionPixelSize(R.styleable.LetterBoard_lineWidth, lineWidth)
            letterSize = a.getDimension(R.styleable.LetterBoard_letterSize, letterSize)
            letterColor = a.getColor(R.styleable.LetterBoard_letterColor, letterColor)
            streakWidth = a.getDimensionPixelSize(R.styleable.LetterBoard_streakWidth, streakWidth)
            snapToGrid = a.getInteger(R.styleable.LetterBoard_snapToGrid, snapToGrid)
            gridLineVisibility = a.getBoolean(R.styleable.LetterBoard_gridLineVisibility, gridLineVisibility)
            setGridWidth(gridWidth)
            setGridHeight(gridHeight)
            setGridLineColor(lineColor)
            setGridLineWidth(lineWidth)
            setLetterSize(letterSize)
            setLetterColor(letterColor)
            setStreakWidth(streakWidth)
            setGridLineVisibility(gridLineVisibility)
            a.recycle()
        }
        _dataAdapter = SampleLetterGridDataAdapter(gridRowCount, gridColCount)
        gridLineBackground.setColCount(gridColCount)
        gridLineBackground.setRowCount(gridRowCount)
        streakView.grid = gridLineBackground
        streakView.isInteractive = true
        streakView.isRememberStreakLine = true
        streakView.isSnapToGrid = fromId(snapToGrid)
        streakView.setOnInteractionListener(StreakViewInteraction())
        attachAllViews()
        initialized = true
        scaleX = scaleX
        scaleY = scaleY
    }

    private fun attachAllViews() {
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(gridLineBackground, layoutParams)
        addView(streakView, layoutParams)
        addView(letterGrid, layoutParams)
    }

    private inner class StreakViewInteraction : OnInteractionListener {
        private fun getStringInRange(start: GridIndex, end: GridIndex): String {
            val dir = fromLine(start, end)
            if (dir === Direction.NONE) return ""
            val count = getIndexLength(start, end)
            val buff = CharArray(count)
            for (i in 0 until count) {
                buff[i] = _dataAdapter.getLetter(start.row + dir.yOff * i, start.col + dir.xOff * i)
            }
            return String(buff)
        }

        override fun onTouchBegin(streakLine: StreakLine) {
            selectionListener?.let {
                val idx = streakLine.startIndex
                val str = _dataAdapter.getLetter(idx.row, idx.col).toString()
                it.onSelectionBegin(streakLine, str)
            }
        }

        override fun onTouchDrag(streakLine: StreakLine) {
            selectionListener?.onSelectionDrag(
                streakLine,
                getStringInRange(streakLine.startIndex, streakLine.endIndex)
            )
        }

        override fun onTouchEnd(streakLine: StreakLine) {
            selectionListener?.let {
                val str = getStringInRange(streakLine.startIndex, streakLine.endIndex)
                it.onSelectionEnd(streakLine, str)
            }
        }
    }

    interface OnLetterSelectionListener {
        fun onSelectionBegin(streakLine: StreakLine, str: String)
        fun onSelectionDrag(streakLine: StreakLine, str: String)
        fun onSelectionEnd(streakLine: StreakLine, str: String)
    }
}