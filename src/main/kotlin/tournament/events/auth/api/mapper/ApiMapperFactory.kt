package tournament.events.auth.api.mapper

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.mapstruct.factory.Mappers

@Factory
class ApiMapperFactory {

    @Singleton
    fun userInfoResourceMapper() = Mappers.getMapper(UserInfoResourceMapper::class.java)
}
