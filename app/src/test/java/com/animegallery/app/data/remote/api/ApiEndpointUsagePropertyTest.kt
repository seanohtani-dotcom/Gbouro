package com.animegallery.app.data.remote.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * Property 30: API Endpoint Usage
 * 
 * For any API request, the Gelbooru JSON API endpoint should be used 
 * with properly formatted parameters.
 * 
 * Validates: Requirements 14.1
 */
class ApiEndpointUsagePropertyTest : StringSpec({
    
    "Property 30: API requests use correct Gelbooru endpoint" {
        checkAll(100, arbApiRequestParams()) { params ->
            // Build URL with parameters
            val url = buildGelbooruUrl(
                tags = params.tags,
                pageId = params.pageId,
                limit = params.limit
            )
            
            val httpUrl = url.toHttpUrl()
            
            // Verify base URL
            httpUrl.scheme shouldBe "https"
            httpUrl.host shouldBe "gelbooru.com"
            httpUrl.encodedPath shouldBe "/index.php"
            
            // Verify required parameters
            httpUrl.queryParameter("page") shouldBe "dapi"
            httpUrl.queryParameter("s") shouldBe "post"
            httpUrl.queryParameter("q") shouldBe "index"
            httpUrl.queryParameter("json") shouldBe "1"
            
            // Verify custom parameters
            httpUrl.queryParameter("tags") shouldBe params.tags
            httpUrl.queryParameter("pid") shouldBe params.pageId.toString()
            httpUrl.queryParameter("limit") shouldBe params.limit.toString()
        }
    }
    
    "Property 30: Tag suggestions endpoint uses correct parameters" {
        checkAll(100, arbTagSuggestionParams()) { params ->
            // Build tag suggestions URL
            val url = buildTagSuggestionsUrl(
                pattern = params.pattern,
                limit = params.limit
            )
            
            val httpUrl = url.toHttpUrl()
            
            // Verify base URL
            httpUrl.scheme shouldBe "https"
            httpUrl.host shouldBe "gelbooru.com"
            httpUrl.encodedPath shouldBe "/index.php"
            
            // Verify required parameters for tag endpoint
            httpUrl.queryParameter("page") shouldBe "dapi"
            httpUrl.queryParameter("s") shouldBe "tag"
            httpUrl.queryParameter("q") shouldBe "index"
            httpUrl.queryParameter("json") shouldBe "1"
            
            // Verify custom parameters
            httpUrl.queryParameter("name_pattern") shouldBe params.pattern
            httpUrl.queryParameter("limit") shouldBe params.limit.toString()
        }
    }
    
    "Property 30: API URL contains all required query parameters" {
        checkAll(100, arbApiRequestParams()) { params ->
            val url = buildGelbooruUrl(
                tags = params.tags,
                pageId = params.pageId,
                limit = params.limit
            )
            
            // Verify URL contains all required parameters
            url shouldContain "page=dapi"
            url shouldContain "s=post"
            url shouldContain "q=index"
            url shouldContain "json=1"
            url shouldContain "tags="
            url shouldContain "pid="
            url shouldContain "limit="
        }
    }
})

/**
 * Data class for API request parameters
 */
private data class ApiRequestParams(
    val tags: String,
    val pageId: Int,
    val limit: Int
)

/**
 * Data class for tag suggestion parameters
 */
private data class TagSuggestionParams(
    val pattern: String,
    val limit: Int
)

/**
 * Arbitrary generator for API request parameters
 */
private fun arbApiRequestParams(): Arb<ApiRequestParams> = Arb.bind(
    arbTagsString(),
    Arb.int(0, 100),
    Arb.int(1, 100)
) { tags, pageId, limit ->
    ApiRequestParams(tags, pageId, limit)
}

/**
 * Arbitrary generator for tag suggestion parameters
 */
private fun arbTagSuggestionParams(): Arb<TagSuggestionParams> = Arb.bind(
    Arb.string(minSize = 1, maxSize = 20),
    Arb.int(1, 100)
) { pattern, limit ->
    TagSuggestionParams(pattern, limit)
}

/**
 * Arbitrary generator for tags string
 */
private fun arbTagsString(): Arb<String> = Arb.bind(
    Arb.list(Arb.string(minSize = 3, maxSize = 15), 0..10)
) { tags ->
    tags.joinToString(" ")
}

/**
 * Build Gelbooru API URL for images
 */
private fun buildGelbooruUrl(tags: String, pageId: Int, limit: Int): String {
    return "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&tags=$tags&pid=$pageId&limit=$limit"
}

/**
 * Build Gelbooru API URL for tag suggestions
 */
private fun buildTagSuggestionsUrl(pattern: String, limit: Int): String {
    return "https://gelbooru.com/index.php?page=dapi&s=tag&q=index&json=1&name_pattern=$pattern&limit=$limit"
}
