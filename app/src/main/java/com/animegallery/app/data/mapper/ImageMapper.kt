package com.animegallery.app.data.mapper

import com.animegallery.app.data.local.entity.FavoriteImageEntity
import com.animegallery.app.data.remote.dto.ImageDto
import com.animegallery.app.domain.model.ImageDetail
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.model.Rating

/**
 * Maps ImageDto from API to domain ImageItem model
 */
fun ImageDto.toDomainModel(): ImageItem {
    return ImageItem(
        id = id,
        thumbnailUrl = previewUrl,
        previewUrl = sampleUrl,
        fileUrl = fileUrl,
        width = width,
        height = height,
        rating = rating.toRating(),
        tags = tags.split(" ").filter { it.isNotBlank() }
    )
}

/**
 * Maps ImageDto to ImageDetail domain model
 */
fun ImageDto.toDetailModel(): ImageDetail {
    return ImageDetail(
        id = id,
        fileUrl = fileUrl,
        width = width,
        height = height,
        fileSize = 0L, // File size not provided by API
        rating = rating.toRating(),
        tags = tags.split(" ").filter { it.isNotBlank() },
        source = source,
        createdAt = createdAt ?: ""
    )
}

/**
 * Converts rating string to Rating enum
 */
fun String.toRating(): Rating {
    return when (this.lowercase()) {
        "safe", "s" -> Rating.SAFE
        "questionable", "q" -> Rating.QUESTIONABLE
        "explicit", "e" -> Rating.EXPLICIT
        else -> Rating.SAFE
    }
}

/**
 * Converts Rating enum to string
 */
fun Rating.toRatingString(): String {
    return when (this) {
        Rating.SAFE -> "safe"
        Rating.QUESTIONABLE -> "questionable"
        Rating.EXPLICIT -> "explicit"
    }
}

/**
 * Maps ImageItem to FavoriteImageEntity for database storage
 */
fun ImageItem.toEntity(): FavoriteImageEntity {
    return FavoriteImageEntity(
        id = id,
        thumbnailUrl = thumbnailUrl,
        previewUrl = previewUrl,
        fileUrl = fileUrl,
        width = width,
        height = height,
        rating = rating.toRatingString(),
        tags = tags.joinToString(" "),
        timestamp = System.currentTimeMillis()
    )
}

/**
 * Maps FavoriteImageEntity from database to domain ImageItem model
 */
fun FavoriteImageEntity.toDomainModel(): ImageItem {
    return ImageItem(
        id = id,
        thumbnailUrl = thumbnailUrl,
        previewUrl = previewUrl,
        fileUrl = fileUrl,
        width = width,
        height = height,
        rating = rating.toRating(),
        tags = tags.split(" ").filter { it.isNotBlank() }
    )
}
