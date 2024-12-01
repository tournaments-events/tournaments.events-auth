package com.sympauthy.util

import java.time.LocalDateTime

fun min(a: LocalDateTime, b: LocalDateTime): LocalDateTime {
    return if (a.isBefore(b)) a else b
}
