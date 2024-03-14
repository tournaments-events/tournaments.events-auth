package com.sympauthy.data.h2

import com.sympauthy.common.loggerForClass
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.*
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource

/**
 * Create a JDBC [DataSource] using the same connection information of the R2DBC [ConnectionFactory].
 * Unfortunately a JDBC connection is required to migrate our database using Flyway.
 * Otherwise, the whole application relies exclusively on the R2DBC connection.
 */
@Factory
@Requires(condition = DefaultDataSourceIsH2::class)
class H2DefaultDataSourceFactory(
    @Inject private val connectionFactoryOptions: ConnectionFactoryOptions
) {

    private val log = loggerForClass()

    @Singleton
    fun provideDataSource(): DataSource {
        log.debug("Initializing H2 JDBC data source from R2DBC connection factory.")
        val connectionString = createConnectionString(connectionFactoryOptions)
        return JdbcDataSource().apply {
            setURL(connectionString)
        }
    }

    private fun createConnectionString(options: ConnectionFactoryOptions): String {
        return buildString {
            append("jdbc:h2:")
            options.getValue(PROTOCOL)?.let { append("$it:") }
            options.getValue(HOST)?.let { append("$it/") }
            options.getValue(DATABASE)?.let { append("$it") }
        }
    }
}
