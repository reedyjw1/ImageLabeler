package com.reedy.imagelabeler.features.annotations.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.features.annotations.UiDocument
import kotlinx.android.synthetic.main.layout_directory_cell.view.*

class DirectoryAdapter(): ListAdapter<UiDocument, DirectoryVH>(UiDocument.DIFFER) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryVH {
        return DirectoryVH.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: DirectoryVH, position: Int) {
        holder.onBind(getItem(position))
    }
}

class DirectoryVH(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun onBind(data: UiDocument) {
        itemView.name.text = data.name
    }

    companion object {
        fun create(parent: ViewGroup, viewType: Int): DirectoryVH {
            return DirectoryVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_directory_cell, parent, false)
            )
        }
    }
}