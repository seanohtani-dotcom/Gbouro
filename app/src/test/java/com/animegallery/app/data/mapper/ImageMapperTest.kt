package com.animegallery.app.data.mapper

import com.animegallery.app.data.local.entity.FavoriteImageEntity
import com.animegallery.app.data.remote.dto.ImageDto
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.model.Rating
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageMapperTest {

    @Test
    fun `ImageDto to ImageItem mapping preserves all fields`() {
        // Given
        val imageDto = ImageDto(
            id = "12345",
            previewUrl = "https://example.com/preview.jpg",
            sampleUrl = "https://example.com/sample.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = "safe",
            tags = "anime girl school_uniform",
            source = "https://source.com",
            createdAt = "2024-01-01"
        )

        // When
        val imageItem = imageDto.toDomainModel()

        // Then
        assertEquals("12345", imageItem.id)
        assertEquals("https://example.com/preview.jpg", imageItem.thumbnailUrl)
        assertEquals("https://example.com/sample.jpg", imageItem.previewUrl)
        assertEquals("https://example.com/file.jpg", imageItem.fileUrl)
        assertEquals(1920, imageItem.width)
        assertEquals(1080, imageItem.height)
        assertEquals(Rating.SAFE, imageItem.rating)
        assertEquals(listOf("anime", "girl", "school_uniform"), imageItem.tags)
    }

    @Test
    fun `rating string 'safe' converts to Rating SAFE`() {
        assertEquals(Rating.SAFE, "safe".toRating())
        assertEquals(Rating.SAFE, "s".toRating())
        assertEquals(Rating.SAFE, "SAFE".toRating())
    }

    @Test
    fun `rating string 'questionable' converts to Rating QUESTIONABLE`() {
        assertEquals(Rating.QUESTIONABLE, "questionable".toRating())
        assertEquals(Rating.QUESTIONABLE, "q".toRating())
        assertEquals(Rating.QUESTIONABLE, "QUESTIONABLE".toRating())
    }

    @Test
    fun `rating string 'explicit' converts to Rating EXPLICIT`() {
        assertEquals(Rating.EXPLICIT, "explicit".toRating())
        assertEquals(Rating.EXPLICIT, "e".toRating())
        assertEquals(Rating.EXPLICIT, "EXPLICIT".toRating())
    }

    @Test
    fun `unknown rating string defaults to Rating SAFE`() {
        assertEquals(Rating.SAFE, "unknown".toRating())
        assertEquals(Rating.SAFE, "".toRating())
        assertEquals(Rating.SAFE, "invalid".toRating())
    }

    @Test
    fun `tag string splitting handles multiple tags`() {
        // Given
        val imageDto = ImageDto(
            id = "1",
            tags = "tag1 tag2 tag3 tag4"
        )

        // When
        val imageItem = imageDto.toDomainModel()

        // Then
        assertEquals(listOf("tag1", "tag2", "tag3", "tag4"), imageItem.tags)
    }

    @Test
    fun `tag string splitting handles empty tags`() {
        // Given
        val imageDto = ImageDto(
            id = "1",
            tags = ""
        )

        // When
        val imageItem = imageDto.toDomainModel()

        // Then
        assertEquals(emptyList<String>(), imageItem.tags)
    }

    @Test
    fun `tag string splitting filters blank entries`() {
        // Given
        val imageDto = ImageDto(
            id = "1",
            tags = "tag1  tag2   tag3"  // Multiple spaces
        )

        // When
        val imageItem = imageDto.toDomainModel()

        // Then
        assertEquals(listOf("tag1", "tag2", "tag3"), imageItem.tags)
    }

    @Test
    fun `ImageItem to FavoriteImageEntity mapping preserves fields`() {
        // Given
        val imageItem = ImageItem(
            id = "12345",
            thumbnailUrl = "https://example.com/thumb.jpg",
            previewUrl = "https://example.com/preview.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = Rating.QUESTIONABLE,
            tags = listOf("anime", "girl")
        )

        // When
        val entity = imageItem.toEntity()

        // Then
        assertEquals("12345", entity.id)
        assertEquals("https://example.com/thumb.jpg", entity.thumbnailUrl)
        assertEquals("https://example.com/preview.jpg", entity.previewUrl)
        assertEquals("https://example.com/file.jpg", entity.fileUrl)
        assertEquals(1920, entity.width)
        assertEquals(1080, entity.height)
        assertEquals("questionable", entity.rating)
        assertEquals("anime girl", entity.tags)
    }

    @Test
    fun `FavoriteImageEntity to ImageItem mapping preserves fields`() {
        // Given
        val entity = FavoriteImageEntity(
            id = "12345",
            thumbnailUrl = "https://example.com/thumb.jpg",
            previewUrl = "https://example.com/preview.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = "explicit",
            tags = "anime girl school",
            timestamp = System.currentTimeMillis()
        )

        // When
        val imageItem = entity.toDomainModel()

        // Then
        assertEquals("12345", imageItem.id)
        assertEquals("https://example.com/thumb.jpg", imageItem.thumbnailUrl)
        assertEquals("https://example.com/preview.jpg", imageItem.previewUrl)
        assertEquals("https://example.com/file.jpg", imageItem.fileUrl)
        assertEquals(1920, imageItem.width)
        assertEquals(1080, imageItem.height)
        assertEquals(Rating.EXPLICIT, imageItem.rating)
        assertEquals(listOf("anime", "girl", "school"), imageItem.tags)
    }

    @Test
    fun `Rating enum to string conversion works correctly`() {
        assertEquals("safe", Rating.SAFE.toRatingString())
        assertEquals("questionable", Rating.QUESTIONABLE.toRatingString())
        assertEquals("explicit", Rating.EXPLICIT.toRatingString())
    }

    @Test
    fun `ImageDto to ImageDetail mapping includes all detail fields`() {
        // Given
        val imageDto = ImageDto(
            id = "12345",
            previewUrl = "https://example.com/preview.jpg",
            sampleUrl = "https://example.com/sample.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = "questionable",
            tags = "anime girl",
            source = "https://source.com",
            createdAt = "2024-01-01 12:00:00"
        )

        // When
        val imageDetail = imageDto.toDetailModel()

        // Then
        assertEquals("12345", imageDetail.id)
        assertEquals("https://example.com/file.jpg", imageDetail.fileUrl)
        assertEquals(1920, imageDetail.width)
        assertEquals(1080, imageDetail.height)
        assertEquals(Rating.QUESTIONABLE, imageDetail.rating)
        assertEquals(listOf("anime", "girl"), imageDetail.tags)
        assertEquals("https://source.com", imageDetail.source)
        assertEquals("2024-01-01 12:00:00", imageDetail.createdAt)
    }
}
