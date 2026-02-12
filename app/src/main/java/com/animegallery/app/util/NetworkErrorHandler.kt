package com.animegallery.app.util

import retrofit2.HttpException
import java.io.IOException

/**
 * Utility class for handling network errors and converting them to user-friendly messages
 */
object NetworkErrorHandler {
    
    fun handleError(exception: Exception): String {
        return when (exception) {
            is IOException -> "Network connection error. Please check your internet."
            is HttpException -> when (exception.code()) {
                429 -> "Rate limit reached. Please try again in a few minutes."
                in 400..499 -> "Invalid request. Please try again."
                in 500..599 -> "Server error. Please try again later."
                else -> "An error occurred. Please try again."
            }
            else -> exception.message ?: "An unexpected error occurred."
        }
    }
}
