package com.animegallery.app.data.remote.api

import com.animegallery.app.data.remote.dto.GelbooruResponse
import com.animegallery.app.data.remote.dto.TagSuggestionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Gelbooru API service interface
 * 
 * Base URL: https://gelbooru.com/
 * API Documentation: https://gelbooru.com/index.php?page=wiki&s=view&id=18780
 */
interface GelbooruApiService {
    
    /**
     * Get images from Gelbooru API
     * 
     * @param page API page type (always "dapi")
     * @param s Section (always "post")
     * @param q Query type (always "index")
     * @param json Enable JSON response (always 1)
     * @param tags Space-separated tags to search for
     * @param pageId Page number for pagination (0-indexed)
     * @param limit Number of results per page (max 100)
     * @return GelbooruResponse containing list of images
     */
    @GET("index.php")
    suspend fun getImages(
        @Query("page") page: String = "dapi",
        @Query("s") s: String = "post",
        @Query("q") q: String = "index",
        @Query("json") json: Int = 1,
        @Query("tags") tags: String,
        @Query("pid") pageId: Int,
        @Query("limit") limit: Int = 50
    ): GelbooruResponse
    
    /**
     * Get tag suggestions from Gelbooru API
     * 
     * @param page API page type (always "dapi")
     * @param s Section (always "tag")
     * @param q Query type (always "index")
     * @param json Enable JSON response (always 1)
     * @param pattern Partial tag name to search for
     * @param limit Number of suggestions to return (max 100)
     * @return TagSuggestionsResponse containing list of matching tags
     */
    @GET("index.php")
    suspend fun getTagSuggestions(
        @Query("page") page: String = "dapi",
        @Query("s") s: String = "tag",
        @Query("q") q: String = "index",
        @Query("json") json: Int = 1,
        @Query("name_pattern") pattern: String,
        @Query("limit") limit: Int = 10
    ): TagSuggestionsResponse
}
