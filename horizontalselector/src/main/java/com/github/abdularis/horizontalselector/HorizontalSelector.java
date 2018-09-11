package com.github.abdularis.horizontalselector;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;

import java.util.ArrayList;

public class HorizontalSelector extends FrameLayout {

    private Animation mNextInAnim;
    private Animation mNextOutAnim;
    private Animation mPreviousInAnim;
    private Animation mPreviousOutAnim;

    private TextSwitcher mMiddleText;

    private ArrayList<String> mValues;
    private int mCurrentIndex;

    public HorizontalSelector(Context context) {
        super(context);
        init(context, null);
    }

    public HorizontalSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public String getCurrentValue() {
        if (mCurrentIndex >= 0 && mCurrentIndex < mValues.size()) {
            return mValues.get(mCurrentIndex);
        }
        return null;
    }

    private void nextValue() {
        int lastIndex = mCurrentIndex;
        mCurrentIndex = Math.min(mCurrentIndex + 1, mValues.size() - 1);
        if (lastIndex != mCurrentIndex) {
            mMiddleText.setInAnimation(mNextInAnim);
            mMiddleText.setOutAnimation(mNextOutAnim);
            updateMiddleText();
        }
    }

    private void previousValue() {
        int lastIndex = mCurrentIndex;
        mCurrentIndex = Math.max(0, mCurrentIndex - 1);
        if (lastIndex != mCurrentIndex) {
            mMiddleText.setInAnimation(mPreviousInAnim);
            mMiddleText.setOutAnimation(mPreviousOutAnim);
            updateMiddleText();
        }
    }

    private void updateMiddleText() {
        String value = getCurrentValue();
        mMiddleText.setText(value == null ? "" : value);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.selector, this, false);
        addView(view);

        view.findViewById(R.id.btnLeft).setOnClickListener(v -> previousValue());
        view.findViewById(R.id.btnRight).setOnClickListener(v -> nextValue());

        mMiddleText = view.findViewById(R.id.textMiddle);
        mNextInAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        mNextOutAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        mPreviousInAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right);
        mPreviousOutAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_left);

        mValues = new ArrayList<>();
        mCurrentIndex = 0;

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.HorizontalSelector, 0, 0);
            CharSequence[] values = arr.getTextArray(R.styleable.HorizontalSelector_values);
            addValuesFromCharSequences(values);
            arr.recycle();
        }

        updateMiddleText();
    }

    private void addValuesFromCharSequences(CharSequence[] values) {
        if (values == null) return;
        for (CharSequence value : values) {
            mValues.add(value.toString());
        }
    }
}
