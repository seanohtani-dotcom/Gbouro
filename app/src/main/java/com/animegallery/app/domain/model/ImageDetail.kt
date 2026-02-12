package com.animegallery.app.domain.model

data class ImageDetail(
    val id: String,
    val fileUrl: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val rating: Rating,
    val tags: List<String>,
    val source: String?,
    val createdAt: String
)
