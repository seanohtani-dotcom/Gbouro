package com.animegallery.app.domain.usecase

import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all favorited images
 */
class GetAllFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    /**
     * Get all favorited images
     * 
     * @return Flow of list of favorited images
     */
    operator fun invoke(): Flow<List<ImageItem>> {
        return favoritesRepository.getAllFavorites()
    }
}
