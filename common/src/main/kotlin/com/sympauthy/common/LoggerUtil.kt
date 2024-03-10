package com.sympauthy.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T : Any> T.loggerForClass(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}
