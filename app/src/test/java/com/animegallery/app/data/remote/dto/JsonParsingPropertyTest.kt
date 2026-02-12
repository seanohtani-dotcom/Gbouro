package com.animegallery.app.data.remote.dto

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Property 32: JSON Parsing Round Trip
 * 
 * For any valid Gelbooru API JSON response, parsing it into domain models 
 * and then serializing back should preserve the essential data fields.
 * 
 * Validates: Requirements 14.3
 */
class JsonParsingPropertyTest : StringSpec({
    
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    "Property 32: JSON parsing round trip preserves essential fields" {
        checkAll(100, arbImageDto()) { imageDto ->
            // Serialize to JSON
            val jsonString = json.encodeToString(imageDto)
            
            // Parse back from JSON
            val parsedDto = json.decodeFromString<ImageDto>(jsonString)
            
            // Essential fields should be preserved
            parsedDto.id shouldBe imageDto.id
            parsedDto.previewUrl shouldBe imageDto.previewUrl
            parsedDto.sampleUrl shouldBe imageDto.sampleUrl
            parsedDto.fileUrl shouldBe imageDto.fileUrl
            parsedDto.width shouldBe imageDto.width
            parsedDto.height shouldBe imageDto.height
            parsedDto.rating shouldBe imageDto.rating
            parsedDto.tags shouldBe imageDto.tags
            parsedDto.source shouldBe imageDto.source
            parsedDto.createdAt shouldBe imageDto.createdAt
        }
    }
    
    "Property 32: GelbooruResponse parsing round trip preserves posts" {
        checkAll(100, arbGelbooruResponse()) { response ->
            // Serialize to JSON
            val jsonString = json.encodeToString(response)
            
            // Parse back from JSON
            val parsedResponse = json.decodeFromString<GelbooruResponse>(jsonString)
            
            // Posts list should be preserved
            parsedResponse.posts.size shouldBe response.posts.size
            parsedResponse.posts.forEachIndexed { index, parsedDto ->
                val originalDto = response.posts[index]
                parsedDto.id shouldBe originalDto.id
                parsedDto.fileUrl shouldBe originalDto.fileUrl
            }
        }
    }
})

/**
 * Arbitrary generator for ImageDto
 */
private fun arbImageDto(): Arb<ImageDto> = Arb.bind(
    Arb.string(minSize = 1, maxSize = 10),  // id
    Arb.string(minSize = 10, maxSize = 100), // previewUrl
    Arb.string(minSize = 10, maxSize = 100), // sampleUrl
    Arb.string(minSize = 10, maxSize = 100), // fileUrl
    Arb.int(100, 5000),                      // width
    Arb.int(100, 5000),                      // height
    arbRatingString(),                        // rating
    arbTagsString(),                          // tags
    Arb.string(minSize = 10, maxSize = 100).orNull(), // source
    Arb.string(minSize = 10, maxSize = 30).orNull()   // createdAt
) { id, previewUrl, sampleUrl, fileUrl, width, height, rating, tags, source, createdAt ->
    ImageDto(
        id = id,
        previewUrl = previewUrl,
        sampleUrl = sampleUrl,
        fileUrl = fileUrl,
        width = width,
        height = height,
        rating = rating,
        tags = tags,
        source = source,
        createdAt = createdAt
    )
}

/**
 * Arbitrary generator for GelbooruResponse
 */
private fun arbGelbooruResponse(): Arb<GelbooruResponse> = Arb.bind(
    Arb.list(arbImageDto(), 0..10)
) { posts ->
    GelbooruResponse(posts = posts)
}

/**
 * Arbitrary generator for rating strings
 */
private fun arbRatingString(): Arb<String> = Arb.of(
    "safe", "s", "questionable", "q", "explicit", "e"
)

/**
 * Arbitrary generator for tag strings
 */
private fun arbTagsString(): Arb<String> = Arb.bind(
    Arb.list(Arb.string(minSize = 3, maxSize = 15), 0..10)
) { tags ->
    tags.joinToString(" ")
}
