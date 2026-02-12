package com.animegallery.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagSuggestionsResponse(
    @SerialName("tag")
    val tags: List<TagDto> = emptyList()
)

@Serializable
data class TagDto(
    @SerialName("name")
    val name: String,
    @SerialName("count")
    val count: Int = 0
)
