package org.example

import org.example.ratelimiter.RateLimiter

fun main() {
    val requestsPerSecond = 10
    val rateLimiter = RateLimiter(requestsPerSecond)
    println(rateLimiter.isRequestAllowed())
    Thread.sleep(1000L / requestsPerSecond)
    println(rateLimiter.isRequestAllowed())
}
