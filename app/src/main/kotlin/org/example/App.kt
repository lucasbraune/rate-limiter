package org.example

import org.example.ratelimiter.RateLimiter

fun main() {
    val rateLimiter = RateLimiter(10)
    println(rateLimiter.isRequestAllowed())
    Thread.sleep(100)
    println(rateLimiter.isRequestAllowed())
}
