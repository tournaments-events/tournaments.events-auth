package com.sympauthy.data

import com.sympauthy.data.migration.DatabaseMigrator
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.event.ApplicationStartupEvent
import jakarta.inject.Singleton

@Singleton
class DatabaseMigrationInitializer(
    private val databaseMigrator: DatabaseMigrator?
) : ApplicationEventListener<ApplicationStartupEvent> {

    override fun onApplicationEvent(event: ApplicationStartupEvent) {
        databaseMigrator?.migrate()
    }
}
