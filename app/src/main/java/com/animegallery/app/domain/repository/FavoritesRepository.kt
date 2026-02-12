package com.animegallery.app.domain.repository

import com.animegallery.app.domain.model.ImageItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for favorites operations
 */
interface FavoritesRepository {
    /**
     * Add an image to favorites
     * 
     * @param image Image to add to favorites
     * @return Result indicating success or failure
     */
    suspend fun addFavorite(image: ImageItem): Result<Unit>
    
    /**
     * Remove an image from favorites
     * 
     * @param imageId ID of the image to remove
     * @return Result indicating success or failure
     */
    suspend fun removeFavorite(imageId: String): Result<Unit>
    
    /**
     * Check if an image is favorited
     * 
     * @param imageId ID of the image to check
     * @return True if favorited, false otherwise
     */
    suspend fun isFavorite(imageId: String): Boolean
    
    /**
     * Get all favorited images
     * 
     * @return Flow of list of favorited images
     */
    fun getAllFavorites(): Flow<List<ImageItem>>
}
