package org.example.ratelimiter

import javax.annotation.concurrent.GuardedBy
import javax.annotation.concurrent.ThreadSafe
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@ThreadSafe
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

    @GuardedBy("this")
    private var tokensInBucket = 0

    /** All tokens added to the bucket, including overflow. */
    @GuardedBy("this")
    private var tokensAddedToBucket = 0

    @GuardedBy("this")
    private fun refill() {
        val timeUnitsElapsed = timeMark.elapsedNow() / timeUnit
        val allTokens = (timeUnitsElapsed * maxRequestRate).toInt()
        tokensInBucket = min(bucketSize, tokensInBucket + (allTokens - tokensAddedToBucket))
        tokensAddedToBucket = allTokens
    }

    @Synchronized
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
