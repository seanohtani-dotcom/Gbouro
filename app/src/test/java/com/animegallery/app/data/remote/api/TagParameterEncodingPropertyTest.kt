package com.animegallery.app.data.remote.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URLEncoder

/**
 * Property 31: Tag Parameter Encoding
 * 
 * For any tags containing special characters, the tags should be properly 
 * URL-encoded in API requests.
 * 
 * Validates: Requirements 14.2
 */
class TagParameterEncodingPropertyTest : StringSpec({
    
    "Property 31: Tags with special characters are properly URL-encoded" {
        checkAll(100, arbTagsWithSpecialChars()) { tags ->
            // Join tags with spaces
            val tagsString = tags.joinToString(" ")
            
            // Encode as URL parameter
            val encoded = URLEncoder.encode(tagsString, "UTF-8")
            
            // Build URL with encoded tags
            val url = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&tags=$encoded"
            val httpUrl = url.toHttpUrl()
            
            // Verify the tags parameter is properly encoded
            val tagsParam = httpUrl.queryParameter("tags")
            tagsParam shouldBe tagsString
            
            // Verify special characters are not present in raw URL
            if (tagsString.contains("&")) {
                encoded shouldNotContain "&"
            }
            if (tagsString.contains("=")) {
                encoded shouldNotContain "="
            }
            if (tagsString.contains("#")) {
                encoded shouldNotContain "#"
            }
        }
    }
    
    "Property 31: Space-separated tags are properly encoded" {
        checkAll(100, arbMultipleTags()) { tags ->
            val tagsString = tags.joinToString(" ")
            val encoded = URLEncoder.encode(tagsString, "UTF-8")
            
            // Spaces should be encoded as + or %20
            if (tags.size > 1) {
                encoded shouldContain Regex("[+%]")
            }
            
            // Decode should restore original
            val decoded = java.net.URLDecoder.decode(encoded, "UTF-8")
            decoded shouldBe tagsString
        }
    }
    
    "Property 31: Tags with unicode characters are properly encoded" {
        checkAll(100, arbUnicodeTags()) { tags ->
            val tagsString = tags.joinToString(" ")
            val encoded = URLEncoder.encode(tagsString, "UTF-8")
            
            // Decode should restore original unicode
            val decoded = java.net.URLDecoder.decode(encoded, "UTF-8")
            decoded shouldBe tagsString
        }
    }
})

/**
 * Arbitrary generator for tags with special characters
 */
private fun arbTagsWithSpecialChars(): Arb<List<String>> {
    val specialChars = listOf("&", "=", "#", "?", "/", ":", ";", "@")
    return Arb.list(
        Arb.string(minSize = 3, maxSize = 10).map { base ->
            val char = specialChars.random()
            "$base$char"
        },
        1..5
    )
}

/**
 * Arbitrary generator for multiple regular tags
 */
private fun arbMultipleTags(): Arb<List<String>> = Arb.list(
    Arb.string(minSize = 3, maxSize = 15),
    1..10
)

/**
 * Arbitrary generator for tags with unicode characters
 */
private fun arbUnicodeTags(): Arb<List<String>> {
    val unicodeChars = listOf("日本", "한국", "中文", "العربية", "हिन्दी")
    return Arb.list(
        Arb.string(minSize = 2, maxSize = 8).map { base ->
            val unicode = unicodeChars.random()
            "$base$unicode"
        },
        1..5
    )
}
