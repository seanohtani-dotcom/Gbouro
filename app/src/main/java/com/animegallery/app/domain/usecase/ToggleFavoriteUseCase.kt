package com.animegallery.app.domain.usecase

import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.repository.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for toggling favorite status of an image
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    /**
     * Toggle favorite status of an image
     * 
     * @param image Image to toggle favorite status
     * @return Result containing new favorite status (true if favorited, false if unfavorited)
     */
    suspend operator fun invoke(image: ImageItem): Result<Boolean> {
        return try {
            val isFavorite = favoritesRepository.isFavorite(image.id)
            
            if (isFavorite) {
                favoritesRepository.removeFavorite(image.id)
                Result.success(false)
            } else {
                favoritesRepository.addFavorite(image)
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
