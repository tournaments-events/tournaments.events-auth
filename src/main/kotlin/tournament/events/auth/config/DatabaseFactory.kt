package tournament.events.auth.config

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.komapper.r2dbc.R2dbcDatabase

@Factory
class DatabaseFactory(
    @Value("\${datasources.postgres.url}") private val datasourceUrl: String
) {

    @Singleton
    fun provideDatabase(): R2dbcDatabase {
        if (datasourceUrl.isBlank()) {
            throw IllegalStateException(
                """No url configured for datasource postgres. 
                Configure datasources.postgres.url in your application.yml.""".trimIndent()
            )
        }
        return R2dbcDatabase(datasourceUrl)
    }
}
