package com.sympauthy.util

import java.util.*

/**
 * Return the [Locale] contained in the [Optional] or the hardcoded default.
 */
fun Optional<Locale>.orDefault(): Locale = this.orElse(Locale.US)
