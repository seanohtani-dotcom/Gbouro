package com.animegallery.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.animegallery.app.data.local.dao.DownloadedImageDao
import com.animegallery.app.data.local.dao.FavoriteImageDao
import com.animegallery.app.data.local.entity.DownloadedImageEntity
import com.animegallery.app.data.local.entity.FavoriteImageEntity

@Database(
    entities = [FavoriteImageEntity::class, DownloadedImageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteImageDao(): FavoriteImageDao
    abstract fun downloadedImageDao(): DownloadedImageDao
}
