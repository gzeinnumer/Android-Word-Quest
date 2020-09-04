package com.aar.app.wsp.custom.easyadapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapterDelegate<T>(
    modelClass: Class<T>,
    @param:LayoutRes
    private val layoutRes: Int,
    private val binder: Binder<T>,
    private val itemClickListener: OnItemClickListener<T>?
) : AdapterDelegate<T, SimpleAdapterDelegate.ViewHolder>(modelClass) {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(model: T, holder: ViewHolder) {
        holder.itemView.setOnClickListener { v: View? -> itemClickListener?.onClick(model, v) }
        binder.bind(model, holder)
    }

    interface Binder<T> {
        fun bind(model: T, holder: ViewHolder?)
    }

    interface OnItemClickListener<T> {
        fun onClick(model: T, view: View?)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val cache = SparseArray<View?>()
        fun <T : View?> find(resId: Int): T? {
            var v = cache[resId]
            if (v != null) {
                return v as T
            }
            v = itemView.findViewById(resId)
            cache.put(resId, v)
            return v as T?
        }
    }
}