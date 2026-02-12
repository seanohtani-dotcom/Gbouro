package com.animegallery.app.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.animegallery.app.data.remote.api.GelbooruApiService
import com.animegallery.app.data.remote.dto.ImageDto
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource for loading images from Gelbooru API with pagination support
 * 
 * @param apiService Gelbooru API service
 * @param tags List of tags to search for
 */
class ImagePagingSource(
    private val apiService: GelbooruApiService,
    private val tags: List<String>
) : PagingSource<Int, ImageDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageDto> {
        return try {
            val page = params.key ?: 0
            val tagsString = tags.joinToString(" ")
            
            val response = apiService.getImages(
                tags = tagsString,
                pageId = page,
                limit = params.loadSize
            )

            LoadResult.Page(
                data = response.posts,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.posts.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
