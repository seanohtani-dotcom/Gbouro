package com.animegallery.app.domain.repository

import com.animegallery.app.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Get current authenticated user
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): Result<User>
    
    /**
     * Register new user with email and password
     */
    suspend fun register(email: String, password: String): Result<User>
    
    /**
     * Sign out current user
     */
    suspend fun signOut(): Result<Unit>
    
    /**
     * Check if user is signed in
     */
    fun isSignedIn(): Boolean
}
