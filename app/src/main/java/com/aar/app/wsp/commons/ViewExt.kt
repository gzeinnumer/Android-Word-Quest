package com.aar.app.wsp.commons

import android.view.View

fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.goneIf(gone: Boolean) {
    this?.visibility = if (gone) View.GONE else View.VISIBLE
}