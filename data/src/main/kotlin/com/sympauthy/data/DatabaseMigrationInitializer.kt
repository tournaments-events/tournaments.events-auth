package com.sympauthy.data

import com.sympauthy.common.loggerForClass
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.event.ApplicationStartupEvent
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.sql.DataSource

@Singleton
class DatabaseMigrationInitializer(
    @Inject private val dataSource: DataSource
) : ApplicationEventListener<ApplicationStartupEvent> {

    private val log = loggerForClass()

    override fun onApplicationEvent(event: ApplicationStartupEvent) {
        log.info("${dataSource.connection.clientInfo}")
    }
}
