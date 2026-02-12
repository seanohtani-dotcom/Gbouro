package com.animegallery.app.domain.usecase

import com.animegallery.app.domain.model.ImageDetail
import com.animegallery.app.domain.repository.ImageRepository
import javax.inject.Inject

/**
 * Use case for getting detailed information about a specific image
 */
class GetImageDetailUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    /**
     * Get detailed information for a specific image
     * 
     * @param imageId ID of the image
     * @return Result containing ImageDetail or error
     */
    suspend operator fun invoke(imageId: String): Result<ImageDetail> {
        return imageRepository.getImageDetail(imageId)
    }
}
