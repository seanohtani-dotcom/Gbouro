package com.animegallery.app.domain.repository

/**
 * Repository interface for tag operations
 */
interface TagRepository {
    /**
     * Get tag suggestions based on partial input
     * 
     * @param query Partial tag name to search for
     * @return Result containing list of tag suggestions
     */
    suspend fun getTagSuggestions(query: String): Result<List<String>>
}
