package com.github.abdularis.horizontalspinner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextSwitcher
import kotlin.math.max
import kotlin.math.min

class HorizontalSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        init(context, attrs)
    }

    private lateinit var nextInAnim: Animation
    private lateinit var nextOutAnim: Animation
    private lateinit var previousInAnim: Animation
    private lateinit var previousOutAnim: Animation
    private lateinit var middleText: TextSwitcher
    private var dataValues: ArrayList<String> = ArrayList()

    var currentIndex = 0
        private set
    var onSelectedItemChangedListener: OnSelectedItemChanged? = null

    interface OnSelectedItemChanged {
        fun onSelectedItemChanged(newItem: String?)
    }

    val currentValue: String?
        get() = if (currentIndex >= 0 && currentIndex <= dataValues.lastIndex) {
            dataValues[currentIndex]
        } else null

    private fun nextValue() {
        val lastIndex = currentIndex
        currentIndex = min(currentIndex + 1, dataValues.lastIndex)
        if (lastIndex != currentIndex) {
            middleText.inAnimation = nextInAnim
            middleText.outAnimation = nextOutAnim
            updateMiddleText()
            if (onSelectedItemChangedListener != null) {
                onSelectedItemChangedListener!!.onSelectedItemChanged(currentValue)
            }
        }
    }

    private fun previousValue() {
        val lastIndex = currentIndex
        currentIndex = max(0, currentIndex - 1)
        if (lastIndex != currentIndex) {
            middleText.inAnimation = previousInAnim
            middleText.outAnimation = previousOutAnim
            updateMiddleText()
            if (onSelectedItemChangedListener != null) {
                onSelectedItemChangedListener!!.onSelectedItemChanged(currentValue)
            }
        }
    }

    private fun updateMiddleText() {
        val value = currentValue
        middleText.setText(value ?: "")
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val view = LayoutInflater.from(context).inflate(R.layout.selector, this, false)
        addView(view)
        view.findViewById<View>(R.id.btnLeft).setOnClickListener { previousValue() }
        view.findViewById<View>(R.id.btnRight).setOnClickListener { nextValue() }
        middleText = view.findViewById(R.id.textMiddle)
        nextInAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        nextOutAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)
        previousInAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
        previousOutAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_left)
        currentIndex = 0
        attrs?.let {
            val arr = context.obtainStyledAttributes(it, R.styleable.HorizontalSelector, 0, 0)
            val values = arr.getTextArray(R.styleable.HorizontalSelector_values)
            addValuesFromCharSequences(values)
            arr.recycle()
        }
        updateMiddleText()
    }

    private fun addValuesFromCharSequences(values: Array<CharSequence>?) {
        if (values == null) return
        for (value in values) {
            dataValues.add(value.toString())
        }
    }
}