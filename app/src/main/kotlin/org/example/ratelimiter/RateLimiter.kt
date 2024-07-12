package org.example.ratelimiter

import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class RateLimiter(
    private val maxRequestRate: Int,
    private val timeUnit: Duration = 1.seconds,
    timeSource: TimeSource = TimeSource.Monotonic,
) {
    init {
        require(maxRequestRate > 0)
    }

    private val timeMark = timeSource.markNow()
    private val bucketSize = maxRequestRate
    private var tokensInBucket = 0
    /** All tokens added to the bucket, including overflow. */
    private var tokensAddedToBucket = 0

    private fun refill() {
        val timeUnitsElapsed = timeMark.elapsedNow() / timeUnit
        val tokensUntilNow = (timeUnitsElapsed * maxRequestRate).toInt()
        tokensInBucket = min(bucketSize, tokensInBucket + (tokensUntilNow - tokensAddedToBucket))
        tokensAddedToBucket = tokensUntilNow
    }

    fun isRequestAllowed(): Boolean {
        refill()
        if (tokensInBucket > 0) {
            tokensInBucket -= 1
            return true
        } else {
            return false
        }
    }
}
