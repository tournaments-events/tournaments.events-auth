package tournament.events.auth.business.manager.mapper

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.mapstruct.factory.Mappers

@Factory
class BusinessMapperFactory {

    @Singleton
    fun jwtKeysMapper() = Mappers.getMapper(JwtKeysMapper::class.java)
}
