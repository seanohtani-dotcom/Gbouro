package com.animegallery.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_images")
data class DownloadedImageEntity(
    @PrimaryKey
    val id: String,
    val localFilePath: String,
    val timestamp: Long
)
