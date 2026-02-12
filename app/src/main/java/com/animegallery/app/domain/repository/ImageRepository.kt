package com.animegallery.app.domain.repository

import androidx.paging.PagingData
import com.animegallery.app.domain.model.ImageDetail
import com.animegallery.app.domain.model.ImageItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for image operations
 */
interface ImageRepository {
    /**
     * Get paginated images from API
     * 
     * @param tags List of tags to search for
     * @return Flow of PagingData containing ImageItem
     */
    fun getImages(tags: List<String>): Flow<PagingData<ImageItem>>
    
    /**
     * Get detailed information for a specific image
     * 
     * @param imageId ID of the image
     * @return Result containing ImageDetail or error
     */
    suspend fun getImageDetail(imageId: String): Result<ImageDetail>
}
