package com.animegallery.app.domain.model

sealed class DownloadProgress {
    data class Progress(val percentage: Float) : DownloadProgress()
    data class Success(val filePath: String) : DownloadProgress()
    data class Error(val message: String) : DownloadProgress()
}
