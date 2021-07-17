package com.reedy.imagelabeler.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Project(
    @PrimaryKey
    val projectUid: String = UUID.randomUUID().toString(),
    @ColumnInfo
    val dateCreated: Long = Date().time
)