package com.animegallery.app.domain.model

/**
 * Domain model for user
 */
data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null
)
