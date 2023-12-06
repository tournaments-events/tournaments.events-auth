package tournament.events.auth.business.manager.mapper

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.mapstruct.factory.Mappers

@Factory
class BusinessMapperFactory {

    @Singleton
    fun cryptoKeysMapper() = Mappers.getMapper(CryptoKeysMapper::class.java)

    @Singleton
    fun indexedCryptoKeysMapper() = Mappers.getMapper(IndexedCryptoKeysMapper::class.java)
}
