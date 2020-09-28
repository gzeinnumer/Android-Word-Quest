package com.aar.app.wsp.commons

import android.view.View

fun View?.goneIf(gone: Boolean) {
    this?.visibility = if (gone) View.GONE else View.VISIBLE
}