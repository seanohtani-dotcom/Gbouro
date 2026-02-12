package com.animegallery.app.domain.repository

import com.animegallery.app.domain.model.DownloadProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for image download operations
 */
interface DownloadRepository {
    /**
     * Download an image to device storage
     * 
     * @param imageUrl URL of the image to download
     * @param imageId ID of the image
     * @return Flow of download progress
     */
    suspend fun downloadImage(imageUrl: String, imageId: String): Flow<DownloadProgress>
    
    /**
     * Check if an image has been downloaded
     * 
     * @param imageId ID of the image to check
     * @return True if downloaded, false otherwise
     */
    suspend fun isDownloaded(imageId: String): Boolean
}
