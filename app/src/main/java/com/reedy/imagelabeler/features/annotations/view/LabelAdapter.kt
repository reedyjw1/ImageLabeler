package com.reedy.imagelabeler.features.annotations.view


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.features.annotations.model.UiLabel
import kotlinx.android.synthetic.main.label_text_cell.view.*
import kotlinx.android.synthetic.main.layout_directory_cell.view.*

class LabelAdapter: ListAdapter<UiLabel, LabelVH>(UiLabel.DIFFER) {

    var onClick: ((UiLabel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelVH {
        return LabelVH.create(parent, viewType).apply {
            onClick = this@LabelAdapter.onClick
        }
    }

    override fun onBindViewHolder(holder: LabelVH, position: Int) {
        holder.onBind(getItem(position))
    }
}

class LabelVH(itemView: View): RecyclerView.ViewHolder(itemView) {

    var onClick: ((UiLabel) -> Unit)? = null

    fun onBind(data: UiLabel) {
        itemView.label.text = data.name
        Log.i("Adapter", "onBind: $data")
        if (data.selected)
            itemView.text_layout_holder.setBackgroundColor(itemView.context.getColor(R.color.light_blue_600))
        else {
            itemView.text_layout_holder.setBackgroundColor(itemView.context.getColor(R.color.white))
        }
        itemView.setOnClickListener { onClick?.invoke(data) }
    }

    companion object {
        fun create(parent: ViewGroup, viewType: Int): LabelVH {
            return LabelVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.label_text_cell, parent, false)
            )
        }
    }
}