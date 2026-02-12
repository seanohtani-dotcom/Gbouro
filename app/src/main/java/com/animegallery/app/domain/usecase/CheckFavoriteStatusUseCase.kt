package com.animegallery.app.domain.usecase

import com.animegallery.app.domain.repository.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for checking if an image is favorited
 */
class CheckFavoriteStatusUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    /**
     * Check if an image is favorited
     * 
     * @param imageId ID of the image to check
     * @return True if favorited, false otherwise
     */
    suspend operator fun invoke(imageId: String): Boolean {
        return favoritesRepository.isFavorite(imageId)
    }
}
