package com.aar.app.wsp.features.gamehistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.aar.app.wsp.R
import com.aar.app.wsp.commons.DurationFormatter.fromInteger
import com.aar.app.wsp.custom.easyadapter.AdapterDelegate
import com.aar.app.wsp.model.GameDataInfo

class GameDataAdapterDelegate : AdapterDelegate<GameDataInfo, GameDataAdapterDelegate.ViewHolder>(GameDataInfo::class.java) {

    var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_game_data_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(model: GameDataInfo, holder: ViewHolder) {
        holder.textName?.text = model.name
        holder.textDuration?.text = fromInteger(model.duration)
        var desc = holder.itemView.context.getString(R.string.game_data_desc)
        desc = desc.replace(":gridSize".toRegex(), "${model.gridRowCount}x${model.gridColCount}")
        desc = desc.replace(":wordCount".toRegex(), model.usedWordsCount.toString())
        holder.textOtherDesc?.text = desc
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(model)
        }
        holder.viewDeleteItem?.setOnClickListener {
            onClickListener?.onDeleteClick(model)
        }
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.text_name)
        var textName: TextView? = null

        @JvmField
        @BindView(R.id.text_duration)
        var textDuration: TextView? = null

        @JvmField
        @BindView(R.id.text_desc)
        var textOtherDesc: TextView? = null

        @JvmField
        @BindView(R.id.delete_list_item)
        var viewDeleteItem: View? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    interface OnClickListener {
        fun onClick(gameDataInfo: GameDataInfo?)
        fun onDeleteClick(gameDataInfo: GameDataInfo?)
    }
}