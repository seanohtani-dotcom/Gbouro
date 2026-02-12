package com.animegallery.app.domain.usecase

import androidx.paging.PagingData
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting paginated images from the repository
 */
class GetImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    /**
     * Get paginated images based on search tags
     * 
     * @param tags List of tags to search for (empty list for all images)
     * @return Flow of PagingData containing ImageItem
     */
    operator fun invoke(tags: List<String>): Flow<PagingData<ImageItem>> {
        return imageRepository.getImages(tags)
    }
}
