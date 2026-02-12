package com.animegallery.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GelbooruResponse(
    @SerialName("post")
    val posts: List<ImageDto> = emptyList()
)
