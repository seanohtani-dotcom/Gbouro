package com.animegallery.app.domain.usecase

import com.animegallery.app.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for getting tag suggestions
 */
class GetTagSuggestionsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Get tag suggestions based on partial input
     * 
     * @param query Partial tag name
     * @return Result containing list of tag suggestions
     */
    suspend operator fun invoke(query: String): Result<List<String>> {
        return tagRepository.getTagSuggestions(query)
    }
}
