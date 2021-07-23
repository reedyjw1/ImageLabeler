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
    val projectName: String,
    @ColumnInfo
    val license: String = "",
    @ColumnInfo
    val datasetUrl: String = "",
    @ColumnInfo
    val dateCreated: Long = Date().time
)