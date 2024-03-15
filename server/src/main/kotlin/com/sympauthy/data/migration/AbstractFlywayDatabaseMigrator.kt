package com.sympauthy.data.migration

import com.sympauthy.util.loggerForClass
import io.micronaut.flyway.FlywayConfigurationProperties
import org.flywaydb.core.api.Location
import java.nio.file.Files
import java.nio.file.Path
import javax.sql.DataSource

/**
 * Migrate a SQL database using Flyway.
 *
 * To support Graalvm, the migrations are not contained in the classpath but must be provided inside a directory.
 *
 * For each file system locations configured, this migrator will check if the configured directory does not
 * contain a directory specific to the driver. If it does, the specific directory will be used for the migration.
 * This is done in order to allow SympAuthy to ship migrations for all databases it supports.
 */
abstract class AbstractFlywayDatabaseMigrator(
    private val driver: String,
    private val dataSource: DataSource,
    private val configuration: FlywayConfigurationProperties
) : DatabaseMigrator {

    private val logger = loggerForClass()

    override val enabled: Boolean
        get() = configuration.isEnabled

    /**
     * Run the database migrations.
     *
     * /!\ We do not use the AbstractFlywayMigration provided by micronaut-flyway to avoid it loading
     * the feature for graalvm. The Feature will cause flyway to ignore all other locations.
     */
    override fun migrate() {
        if (!configuration.isEnabled) {
            return
        }

        val locations = getLocations() ?: return
        val fluentConfiguration = configuration.fluentConfiguration.also {
            it.dataSource(dataSource)
            it.cleanDisabled(!configuration.isCleanSchema)
            it.configuration(configuration.properties)
            it.locations(*locations.toTypedArray())
        }

        val flyway = fluentConfiguration.load()
        if (configuration.isCleanSchema) {
            logger.info("Cleaning schema for ${configuration.nameQualifier} database.")
            flyway.clean()
        }

        fluentConfiguration.locations.joinToString(", ") { it.descriptor }.let {
            logger.info("Running migrations for ${configuration.nameQualifier} database and found at following locations: $it")
        }
        flyway.migrate()
    }

    private fun getLocations(): List<Location>? {
        val classPathLocations = configuration.fluentConfiguration.locations
            .filter { it.isClassPath }
            .joinToString(",") { it.descriptor }
        if (classPathLocations.isNotEmpty()) {
            logger.error("Classpath location are not supported for database migration. Migrate the following locations to directories: $classPathLocations.")
            return null
        }

        val fileSystemLocations = configuration.fluentConfiguration.locations
            .filter { it.isFileSystem }
            .map(this::getDriverSpecificLocation)
            .toList()
        val otherLocations = configuration.fluentConfiguration.locations
            .filter { !it.isFileSystem }
            .toList()

        val locations = fileSystemLocations + otherLocations
        if (locations.isEmpty()) {
            logger.error("Classpath location are not supported for database migration. Migrate the following locations to directories: $classPathLocations.")
            return null
        }
        return locations
    }

    /**
     * Check if the [location] contains a directory specific to the [driver].
     * In it does, the location will be changed to point to the specific directory instead.
     * Otherwise, the location is returned without modification.
     */
    private fun getDriverSpecificLocation(location: Location): Location {
        // Do nothing if the location is a regex.
        if (location.pathRegex != null) {
            return location
        }

        val specificDriverPath = Path.of(location.rootPath, driver)
        return if (Files.isDirectory(specificDriverPath)) {
            Location("${location.prefix}$specificDriverPath")
        } else {
            location
        }
    }
}
