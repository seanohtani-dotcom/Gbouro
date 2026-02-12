package com.animegallery.app.data.repository

import com.animegallery.app.data.remote.api.GelbooruApiService
import com.animegallery.app.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Implementation of TagRepository
 */
class TagRepositoryImpl @Inject constructor(
    private val apiService: GelbooruApiService
) : TagRepository {

    override suspend fun getTagSuggestions(query: String): Result<List<String>> {
        return try {
            if (query.isBlank()) {
                return Result.success(emptyList())
            }
            
            val response = apiService.getTagSuggestions(
                pattern = "$query%",
                limit = 10
            )
            
            val suggestions = response.tags.map { it.name }
            Result.success(suggestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
