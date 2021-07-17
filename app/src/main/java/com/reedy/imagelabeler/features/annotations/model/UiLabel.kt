package com.reedy.imagelabeler.features.annotations.model

import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class UiLabel(
    val uid: UUID = UUID.randomUUID(),
    val name: String,
    val group: String,
    val labelNumber: Int,
    var selected: Boolean = false
) {
    companion object {
        val DIFFER = object: DiffUtil.ItemCallback<UiLabel>() {
            override fun areItemsTheSame(oldItem: UiLabel, newItem: UiLabel): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: UiLabel, newItem: UiLabel): Boolean {
                return oldItem == newItem
            }

        }
    }
}