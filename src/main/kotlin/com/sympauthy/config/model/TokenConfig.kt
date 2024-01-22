package com.sympauthy.config.model

import java.time.Duration

data class TokenConfig(
    val accessExpiration: Duration,
    val refreshEnabled: Boolean,
    val refreshExpiration: Duration?
)
