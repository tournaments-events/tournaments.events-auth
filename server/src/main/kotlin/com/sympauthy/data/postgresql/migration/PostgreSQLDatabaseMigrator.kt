package com.sympauthy.data.postgresql.migration

import com.sympauthy.data.migration.AbstractFlywayDatabaseMigrator
import com.sympauthy.data.postgresql.DefaultDataSourceIsPostgreSQL
import io.micronaut.context.annotation.Requires
import io.micronaut.flyway.FlywayConfigurationProperties
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.sql.DataSource

@Singleton
@Requires(condition = DefaultDataSourceIsPostgreSQL::class)
class PostgreSQLDatabaseMigrator(
    @Inject private val dataSource: DataSource,
    @Inject private val configuration: FlywayConfigurationProperties
) : AbstractFlywayDatabaseMigrator(
    driver = "postgresql",
    dataSource = dataSource,
    configuration = configuration
)
