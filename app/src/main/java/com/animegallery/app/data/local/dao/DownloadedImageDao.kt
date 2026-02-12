package com.animegallery.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.animegallery.app.data.local.entity.DownloadedImageEntity

@Dao
interface DownloadedImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(image: DownloadedImageEntity)
    
    @Query("SELECT EXISTS(SELECT 1 FROM downloaded_images WHERE id = :imageId)")
    suspend fun isDownloaded(imageId: String): Boolean
    
    @Query("SELECT * FROM downloaded_images WHERE id = :imageId")
    suspend fun getDownloadedImage(imageId: String): DownloadedImageEntity?
}
