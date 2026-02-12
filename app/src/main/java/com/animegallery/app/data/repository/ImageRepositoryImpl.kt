package com.animegallery.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.animegallery.app.data.mapper.toDetailModel
import com.animegallery.app.data.mapper.toDomainModel
import com.animegallery.app.data.remote.api.GelbooruApiService
import com.animegallery.app.data.remote.paging.ImagePagingSource
import com.animegallery.app.domain.model.ImageDetail
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ImageRepository
 */
class ImageRepositoryImpl @Inject constructor(
    private val apiService: GelbooruApiService
) : ImageRepository {

    override fun getImages(tags: List<String>): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            pagingSourceFactory = {
                ImagePagingSource(apiService, tags)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override suspend fun getImageDetail(imageId: String): Result<ImageDetail> {
        return try {
            val response = apiService.getImages(
                tags = "id:$imageId",
                pageId = 0,
                limit = 1
            )
            val imageDto = response.posts.firstOrNull()
                ?: return Result.failure(Exception("Image not found"))
            Result.success(imageDto.toDetailModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
