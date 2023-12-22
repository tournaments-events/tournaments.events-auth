package tournament.events.auth.business.mapper

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.mapstruct.factory.Mappers

@Factory
class BusinessMapperFactory {

    @Singleton
    fun authorizationCodeMapper() = Mappers.getMapper(AuthorizationCodeMapper::class.java)

    @Singleton
    fun authorizeAttemptMapper() = Mappers.getMapper(AuthorizeAttemptMapper::class.java)

    @Singleton
    fun cryptoKeysMapper() = Mappers.getMapper(CryptoKeysMapper::class.java)

    @Singleton
    fun encodedAuthenticationTokenMapper() = Mappers.getMapper(EncodedAuthenticationTokenMapper::class.java)

    @Singleton
    fun providerUserInfoMapper() = Mappers.getMapper(ProviderUserInfoMapper::class.java)

    @Singleton
    fun indexedCryptoKeysMapper() = Mappers.getMapper(IndexedCryptoKeysMapper::class.java)

    @Singleton
    fun userMapper() = Mappers.getMapper(UserMapper::class.java)
}
