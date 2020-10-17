package com.aar.app.wsp.custom

import android.content.Context
import com.aar.app.wsp.commons.math.Vec2.Companion.sub
import com.aar.app.wsp.commons.math.Vec2.Companion.normalize
import android.graphics.RectF
import android.view.MotionEvent
import com.aar.app.wsp.commons.math.Vec2
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.aar.app.wsp.R
import com.aar.app.wsp.custom.TouchProcessor.OnTouchProcessed
import com.aar.app.wsp.commons.GridIndex
import com.aar.app.wsp.commons.orZero
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min

/**
 * Created by abdularis on 20/06/17.
 *
 * Garis yang bisa didrag (coretan didalam word search game)
 */
class StreakView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class SnapType(var id: Int) {
        NONE(0), START_END(1), ALWAYS_SNAP(2);

        companion object {
            @JvmStatic
            fun fromId(id: Int): SnapType {
                for (t in values()) {
                    if (t.id == id) return t
                }
                throw IllegalArgumentException()
            }
        }
    }

    private val rect: RectF = RectF()
    private var streakLineWidth = DEFAULT_STREAK_LINE_WIDTH_PIXEL
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var gridId = -1
    private var snapToGridType: SnapType = SnapType.NONE
    private val touchProcessor: TouchProcessor = TouchProcessor(OnTouchProcessedListener(), 3.0f)
    private var lines: Stack<StreakLine> = Stack()
    private var interactionListener: OnInteractionListener? = null
    private var _enableOverrideStreakLineColor = false
    private var _overrideStreakLineColor = 0

    var grid: GridBehavior? = null
    var isInteractive = false
    var isRememberStreakLine = false

    var streakWidth: Int
        get() = streakLineWidth
        set(width) {
            streakLineWidth = width
            invalidate()
        }
    var isSnapToGrid: SnapType
        get() = snapToGridType
        set(snapToGrid) {
            check(!(snapToGridType != snapToGrid && gridId == -1 && grid == null)) { "setGrid() first to set the grid object!" }
            snapToGridType = snapToGrid
        }

    init {
        paint.color = Color.GREEN
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.StreakView, 0, 0)
            paint.color = a.getInteger(R.styleable.StreakView_streakColor, paint.color)
            streakLineWidth = a.getDimensionPixelSize(R.styleable.StreakView_streakWidth, streakLineWidth)
            gridId = a.getResourceId(R.styleable.StreakView_strekGrid, gridId)
            isSnapToGrid = SnapType.fromId(a.getInt(R.styleable.StreakView_snapToGrid, 0))
            isInteractive = a.getBoolean(R.styleable.StreakView_interactive, isInteractive)
            isRememberStreakLine = a.getBoolean(R.styleable.StreakView_rememberStreakLine, isRememberStreakLine)
            a.recycle()
        }
    }

    fun setEnableOverrideStreakLineColor(enableOverrideStreakLineColor: Boolean) {
        _enableOverrideStreakLineColor = enableOverrideStreakLineColor
    }

    fun setOverrideStreakLineColor(overrideStreakLineColor: Int) {
        _overrideStreakLineColor = overrideStreakLineColor
    }

    fun setOnInteractionListener(listener: OnInteractionListener?) {
        interactionListener = listener
    }

    private fun pushStreakLine(streakLine: StreakLine, snapToGrid: Boolean) {
        lines.push(streakLine)
        grid?.let {
            streakLine.start.x = it.getCenterColFromIndex(streakLine.startIndex.col).toFloat()
            streakLine.start.y = it.getCenterRowFromIndex(streakLine.startIndex.row).toFloat()
            streakLine.end.x = it.getCenterColFromIndex(streakLine.endIndex.col).toFloat()
            streakLine.end.y = it.getCenterRowFromIndex(streakLine.endIndex.row).toFloat()
        }
    }

    fun invalidateStreakLine() {
        for (streakLine in lines) {
            grid?.let {
                streakLine.start.x = it.getCenterColFromIndex(streakLine.startIndex.col).toFloat()
                streakLine.start.y = it.getCenterRowFromIndex(streakLine.startIndex.row).toFloat()
                streakLine.end.x = it.getCenterColFromIndex(streakLine.endIndex.col).toFloat()
                streakLine.end.y = it.getCenterRowFromIndex(streakLine.endIndex.row).toFloat()
            }
        }
    }

    fun addStreakLines(streakLines: List<StreakLine>, snapToGrid: Boolean) {
        for (line in streakLines) pushStreakLine(line, snapToGrid)
        invalidate()
    }

    fun addStreakLine(streakLine: StreakLine, snapToGrid: Boolean) {
        pushStreakLine(streakLine, snapToGrid)
        invalidate()
    }

    fun popStreakLine() {
        if (lines.isNotEmpty()) {
            lines.pop()
            invalidate()
        }
    }

    fun removeAllStreakLine() {
        lines.clear()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isInteractive) touchProcessor.onTouchEvent(event) else super.onTouchEvent(event)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (gridId != -1 && snapToGridType != SnapType.NONE) {
            grid = rootView.findViewById<View>(gridId) as GridBehavior
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (snapToGridType != SnapType.NONE) {
            grid?.let {
                measuredWidth = it.requiredWidth
                measuredHeight = it.requiredHeight
            }
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        for (line in lines) {
            val v = sub(line.end, line.start)
            val length = v.length()
            var rot = Math.toDegrees(getRotation(v, Vec2.Right).toDouble())
            if (v.y < 0) rot = -rot
            canvas.save()
            if (!java.lang.Double.isNaN(rot)) canvas.rotate(rot.toFloat(), line.start.x, line.start.y)
            val halfWidth = streakLineWidth / 2
            if (_enableOverrideStreakLineColor) {
                paint.color = _overrideStreakLineColor
            } else {
                paint.color = line.color
            }
            rect[line.start.x - halfWidth, line.start.y - halfWidth, line.start.x + length + halfWidth] = line.start.y + halfWidth
            canvas.drawRoundRect(rect, halfWidth.toFloat(), halfWidth.toFloat(), paint)
            canvas.restore()
        }
    }

    private fun getRotation(p1: Vec2, p2: Vec2): Float {
        val dot = normalize(p1).dot(normalize(p2))
        return acos(dot.toDouble()).toFloat()
    }

    private inner class OnTouchProcessedListener : OnTouchProcessed {
        override fun onDown(event: MotionEvent) {
            if (!isRememberStreakLine) {
                if (lines.isEmpty()) lines.push(StreakLine())
            } else {
                lines.push(StreakLine())
            }

            val line = lines.peek()
            val colIdx = grid?.getColIndex(event.x.toInt()).orZero()
            val rowIdx = grid?.getRowIndex(event.y.toInt()).orZero()
            line.startIndex.set(rowIdx, colIdx)

            if (snapToGridType != SnapType.NONE) {
                val centerCol = grid?.getCenterColFromIndex(colIdx)?.toFloat() ?: 0f
                val centerRow = grid?.getCenterRowFromIndex(rowIdx)?.toFloat() ?: 0f

                line.start.set(centerCol, centerRow)
                line.end.set(line.start.x, line.start.y)
            } else {
                line.start.set(event.x, event.y)
                line.end.set(event.x, event.y)
            }
            interactionListener?.onTouchBegin(line)
            invalidate()
        }

        override fun onUp(event: MotionEvent) {
            if (lines.isEmpty()) return
            val line = lines.peek()
            val colIdx = grid?.getColIndex(event.x.toInt()).orZero()
            val rowIdx = grid?.getRowIndex(event.y.toInt()).orZero()
            line.endIndex.set(rowIdx, colIdx)

            if (snapToGridType != SnapType.NONE) {
                val centerCol = grid?.getCenterColFromIndex(colIdx)?.toFloat() ?: 0f
                val centerRow = grid?.getCenterRowFromIndex(rowIdx)?.toFloat() ?: 0f
                line.end.set(centerCol, centerRow)
            } else {
                line.end.set(event.x, event.y)
            }

            interactionListener?.onTouchEnd(line)
            invalidate()
        }

        override fun onMove(event: MotionEvent) {
            if (lines.isEmpty()) return

            val line = lines.peek()
            val colIdx = grid?.getColIndex(event.x.toInt()).orZero()
            val rowIdx = grid?.getRowIndex(event.y.toInt()).orZero()
            line.endIndex.set(rowIdx, colIdx)

            if (snapToGridType == SnapType.ALWAYS_SNAP) {
                val centerCol = grid?.getCenterColFromIndex(colIdx)?.toFloat() ?: 0f
                val centerRow = grid?.getCenterRowFromIndex(rowIdx)?.toFloat() ?: 0f
                line.end.set(centerCol, centerRow)
            } else {
                val halfWidth = streakLineWidth / 2
                val x = max(min(event.x, width - halfWidth.toFloat()), halfWidth.toFloat())
                val y = max(min(event.y, height - halfWidth.toFloat()), halfWidth.toFloat())
                line.end.set(x, y)
            }

            interactionListener?.onTouchDrag(line)
            invalidate()
        }
    }

    //
    interface OnInteractionListener {
        fun onTouchBegin(streakLine: StreakLine)
        fun onTouchDrag(streakLine: StreakLine)
        fun onTouchEnd(streakLine: StreakLine)
    }

    class StreakLine {
        var start: Vec2 = Vec2()
        var end: Vec2 = Vec2()
        var startIndex: GridIndex = GridIndex(-1, -1)
        var endIndex: GridIndex = GridIndex(-1, -1)
        var color = Color.RED
    }

    companion object {
        private const val DEFAULT_STREAK_LINE_WIDTH_PIXEL = 26
    }
}