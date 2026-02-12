package com.animegallery.app.util

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rate limiter to prevent API abuse
 * 
 * Limits the number of requests within a time window
 */
@Singleton
class RateLimiter @Inject constructor() {
    
    private val requestTimestamps = mutableListOf<Long>()
    private val maxRequests = 10
    private val timeWindowMs = 1000L // 1 second
    
    /**
     * Throttle a block of code to respect rate limits
     * 
     * @param block The code block to execute
     * @return Result of the block execution
     */
    suspend fun <T> throttle(block: suspend () -> T): T {
        val now = System.currentTimeMillis()
        
        // Remove timestamps outside the time window
        requestTimestamps.removeAll { it < now - timeWindowMs }
        
        // If we've hit the limit, wait until we can make another request
        if (requestTimestamps.size >= maxRequests) {
            val oldestRequest = requestTimestamps.first()
            val delayMs = timeWindowMs - (now - oldestRequest)
            if (delayMs > 0) {
                delay(delayMs)
            }
            requestTimestamps.removeAt(0)
        }
        
        // Add current timestamp and execute block
        requestTimestamps.add(System.currentTimeMillis())
        return block()
    }
}
