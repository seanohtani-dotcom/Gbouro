package com.animegallery.app.domain.usecase

import com.animegallery.app.domain.model.DownloadProgress
import com.animegallery.app.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for downloading an image to device storage
 */
class DownloadImageUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {
    /**
     * Download an image to device storage
     * 
     * @param imageUrl URL of the image to download
     * @param imageId ID of the image
     * @return Flow of download progress
     */
    suspend operator fun invoke(imageUrl: String, imageId: String): Flow<DownloadProgress> {
        return downloadRepository.downloadImage(imageUrl, imageId)
    }
}
