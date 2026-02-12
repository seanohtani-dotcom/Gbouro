package com.animegallery.app.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.animegallery.app.data.local.dao.DownloadedImageDao
import com.animegallery.app.data.local.entity.DownloadedImageEntity
import com.animegallery.app.domain.model.DownloadProgress
import com.animegallery.app.domain.repository.DownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Implementation of DownloadRepository
 */
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val downloadedImageDao: DownloadedImageDao
) : DownloadRepository {

    override suspend fun downloadImage(imageUrl: String, imageId: String): Flow<DownloadProgress> = flow {
        try {
            val request = Request.Builder().url(imageUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                emit(DownloadProgress.Error("Download failed: ${response.code}"))
                return@flow
            }

            val body = response.body ?: run {
                emit(DownloadProgress.Error("Empty response body"))
                return@flow
            }

            val contentLength = body.contentLength()
            val inputStream = body.byteStream()
            
            // Get file extension from URL
            val extension = imageUrl.substringAfterLast('.', "jpg")
            val fileName = "gelbooru_$imageId.$extension"

            val filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+
                saveToMediaStore(fileName, inputStream, contentLength) { progress ->
                    emit(DownloadProgress.Progress(progress))
                }
            } else {
                // Use legacy storage for older Android versions
                saveToLegacyStorage(fileName, inputStream, contentLength) { progress ->
                    emit(DownloadProgress.Progress(progress))
                }
            }

            if (filePath != null) {
                // Save to database
                downloadedImageDao.insertDownload(
                    DownloadedImageEntity(
                        id = imageId,
                        localFilePath = filePath,
                        timestamp = System.currentTimeMillis()
                    )
                )
                emit(DownloadProgress.Success(filePath))
            } else {
                emit(DownloadProgress.Error("Failed to save file"))
            }

        } catch (e: Exception) {
            emit(DownloadProgress.Error(e.message ?: "Download failed"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isDownloaded(imageId: String): Boolean {
        return downloadedImageDao.isDownloaded(imageId)
    }

    private suspend fun saveToMediaStore(
        fileName: String,
        inputStream: java.io.InputStream,
        contentLength: Long,
        onProgress: suspend (Float) -> Unit
    ): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AnimeGallery")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return null

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (contentLength > 0) {
                    onProgress(totalBytesRead.toFloat() / contentLength)
                }
            }
        }

        return uri.toString()
    }

    private suspend fun saveToLegacyStorage(
        fileName: String,
        inputStream: java.io.InputStream,
        contentLength: Long,
        onProgress: suspend (Float) -> Unit
    ): String? {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val animeGalleryDir = File(picturesDir, "AnimeGallery")
        
        if (!animeGalleryDir.exists()) {
            animeGalleryDir.mkdirs()
        }

        val file = File(animeGalleryDir, fileName)
        
        FileOutputStream(file).use { outputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (contentLength > 0) {
                    onProgress(totalBytesRead.toFloat() / contentLength)
                }
            }
        }

        return file.absolutePath
    }
}
