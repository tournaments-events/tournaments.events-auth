package com.sympauthy.data.h2.migration

import com.sympauthy.data.h2.DefaultDataSourceIsH2
import com.sympauthy.data.migration.DatabaseMigrator
import io.micronaut.context.annotation.Requires
import io.micronaut.flyway.FlywayConfigurationProperties
import io.micronaut.flyway.FlywayMigrator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.sql.DataSource

@Singleton
@Requires(condition = DefaultDataSourceIsH2::class)
class H2DatabaseMigration(
    @Inject private val dataSource: DataSource,
    @Inject private val configuration: FlywayConfigurationProperties,
    @Inject private val migrator: FlywayMigrator
): DatabaseMigrator {

    override fun migrate() {
        // Change location of migration scripts to match the one designed for PostgreSQL.
        val config = configuration.apply {
            fluentConfiguration.locations("classpath:database/h2")
        }
        migrator.run(config, dataSource)
    }
}
