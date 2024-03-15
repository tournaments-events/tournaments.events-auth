package com.sympauthy.data.migration

/**
 *
 */
interface DatabaseMigrator {

    /**
     * True if database migrations are enabled.
     */
    val enabled: Boolean

    fun migrate()
}
