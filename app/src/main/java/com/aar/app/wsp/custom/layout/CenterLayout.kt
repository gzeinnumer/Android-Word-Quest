package com.aar.app.wsp.custom.layout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min

/**
 * Created by abdularis on 22/06/17.
 *
 * Center layout merupakan layout untuk membuat semua child view berada ditengah-tengah
 * relative terhadap posisi CenterLayout object
 */
open class CenterLayout : ViewGroup {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measuredWidth = 0
        var measuredHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            measuredWidth = max(measuredWidth, child.measuredWidth)
            measuredHeight = max(measuredHeight, child.measuredHeight)
        }
        measuredWidth += paddingLeft + paddingRight
        measuredHeight += paddingTop + paddingBottom
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.EXACTLY) measuredWidth = width else if (widthMeasureSpec == MeasureSpec.AT_MOST) measuredWidth = min(measuredWidth, width)
        if (heightMode == MeasureSpec.EXACTLY) measuredHeight = height else if (heightMode == MeasureSpec.AT_MOST) measuredHeight = min(measuredHeight, height)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childRight = getWidth() - paddingRight
        val childBottom = getHeight() - paddingBottom
        var xOff: Int
        var yOff: Int
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            xOff = max(0, width - child.measuredWidth) / 2
            yOff = max(0, height - child.measuredHeight) / 2
            child.layout(childLeft + xOff, childTop + yOff, childRight - xOff, childBottom - yOff)
        }
    }
}