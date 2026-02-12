package com.animegallery.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    @SerialName("id")
    val id: String,
    @SerialName("preview_url")
    val previewUrl: String = "",
    @SerialName("sample_url")
    val sampleUrl: String = "",
    @SerialName("file_url")
    val fileUrl: String = "",
    @SerialName("width")
    val width: Int = 0,
    @SerialName("height")
    val height: Int = 0,
    @SerialName("rating")
    val rating: String = "safe",
    @SerialName("tags")
    val tags: String = "",
    @SerialName("source")
    val source: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)
