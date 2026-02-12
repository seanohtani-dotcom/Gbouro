package com.animegallery.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.animegallery.app.data.local.entity.FavoriteImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(image: FavoriteImageEntity)
    
    @Delete
    suspend fun deleteFavorite(image: FavoriteImageEntity)
    
    @Query("SELECT * FROM favorite_images ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteImageEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_images WHERE id = :imageId)")
    suspend fun isFavorite(imageId: String): Boolean
    
    @Query("DELETE FROM favorite_images WHERE id = :imageId")
    suspend fun deleteFavoriteById(imageId: String)
}
