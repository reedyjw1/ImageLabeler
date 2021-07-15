package com.reedy.imagelabeler.features.annotations.model

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil

data class UiDocument(
    val name: String,
    val uri: Uri,
    val type: String,
    var selected: Boolean = false
) {
    companion object {
        val DIFFER = object: DiffUtil.ItemCallback<UiDocument>() {
            override fun areItemsTheSame(oldItem: UiDocument, newItem: UiDocument): Boolean {
                return oldItem.uri == newItem.uri && oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: UiDocument, newItem: UiDocument): Boolean {
                return oldItem == newItem
            }

        }
    }
}
