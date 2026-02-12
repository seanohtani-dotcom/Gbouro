package com.animegallery.app.data.repository

import com.animegallery.app.data.local.dao.FavoriteImageDao
import com.animegallery.app.data.mapper.toDomainModel
import com.animegallery.app.data.mapper.toEntity
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of FavoritesRepository
 */
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteImageDao: FavoriteImageDao
) : FavoritesRepository {

    override suspend fun addFavorite(image: ImageItem): Result<Unit> {
        return try {
            favoriteImageDao.insertFavorite(image.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFavorite(imageId: String): Result<Unit> {
        return try {
            favoriteImageDao.deleteFavoriteById(imageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isFavorite(imageId: String): Boolean {
        return favoriteImageDao.isFavorite(imageId)
    }

    override fun getAllFavorites(): Flow<List<ImageItem>> {
        return favoriteImageDao.getAllFavorites().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
