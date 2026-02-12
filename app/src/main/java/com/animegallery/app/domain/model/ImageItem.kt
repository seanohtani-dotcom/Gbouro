package com.animegallery.app.domain.model

data class ImageItem(
    val id: String,
    val thumbnailUrl: String,
    val previewUrl: String,
    val fileUrl: String,
    val width: Int,
    val height: Int,
    val rating: Rating,
    val tags: List<String>
)

enum class Rating {
    SAFE, QUESTIONABLE, EXPLICIT
}
