package com.aar.app.wsp.custom.easyadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class AdapterDelegate<T, VH : RecyclerView.ViewHolder>(private val modelClass: Class<T>) {
    fun canHandleModel(model: Any): Boolean {
        return modelClass.isAssignableFrom(model.javaClass)
    }

    val viewType: Int
        get() = modelClass.name.hashCode()

    abstract fun onCreateViewHolder(parent: ViewGroup): VH
    abstract fun onBindViewHolder(model: T, holder: VH)
}