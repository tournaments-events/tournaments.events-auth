package com.sympauthy.business.manager.util

import com.sympauthy.exception.LocalizedException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

fun assertThrowsLocalizedException(
    detailsId: String,
    executable: () -> Unit
) {
    val exception = assertThrows<LocalizedException> {
        executable()
    }
    assertEquals(detailsId, exception.detailsId)
}
