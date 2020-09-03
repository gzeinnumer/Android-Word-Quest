package com.aar.app.wsp.commons

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}