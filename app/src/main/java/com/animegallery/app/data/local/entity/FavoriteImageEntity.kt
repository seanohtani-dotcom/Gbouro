package com.animegallery.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_images")
data class FavoriteImageEntity(
    @PrimaryKey
    val id: String,
    val thumbnailUrl: String,
    val previewUrl: String,
    val fileUrl: String,
    val width: Int,
    val height: Int,
    val rating: String,
    val tags: String,
    val timestamp: Long
)
