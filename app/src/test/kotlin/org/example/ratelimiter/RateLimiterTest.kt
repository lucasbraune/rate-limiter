package org.example.ratelimiter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

class RateLimiterTest {
    @Test
    fun `test rate limiter allows sustained max rate`() {
        val timeSource = TestTimeSource()
        val requestRate = 10
        val requestCount = 100
        val rateLimiter = RateLimiter(requestRate, timeSource = timeSource)

        repeat(requestCount) {
            timeSource += 1.seconds / requestRate
            assertTrue(rateLimiter.isRequestAllowed())
        }
    }

    @Test
    fun `test rate limiter disallows requests above max rate`() {
        val timeSource = TestTimeSource()
        val maxRequestRate = 10
        val rateLimiter = RateLimiter(maxRequestRate, timeSource = timeSource)
        val requestRate = 40
        val requestCount = 10 * requestRate

        var allowed = 0
        repeat(requestCount) {
            timeSource += 1.seconds / requestRate
            if (rateLimiter.isRequestAllowed()) {
                ++allowed
            }
        }

        val expected =  maxRequestRate.toDouble() / requestRate
        assertEquals(expected, allowed / requestCount.toDouble(), 0.1 * expected)
    }

    @Test
    fun `test rate limiter allows burst of requests`() {
        val timeSource = TestTimeSource()
        val requestsPerSecond = 10
        val rateLimiter = RateLimiter(requestsPerSecond, timeSource = timeSource)

        timeSource += 1.seconds
        repeat(requestsPerSecond) {
            assertTrue(rateLimiter.isRequestAllowed())
        }
    }

    @Test
    fun `test rate limiter disallows burst above max rate`() {
        val timeSource = TestTimeSource()
        val requestsPerSecond = 10
        val rateLimiter = RateLimiter(requestsPerSecond, timeSource = timeSource)

        var allowed = 0
        timeSource += 10.seconds
        repeat(requestsPerSecond + 1) {
            if (rateLimiter.isRequestAllowed()) {
                ++allowed
            }
        }

        assertEquals(requestsPerSecond, allowed)
    }
}
