package com.sympauthy.util

fun String?.nullIfBlank(): String? = if (!this.isNullOrBlank()) this else null
