package tournament.events.auth.business.mapper

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.mapstruct.factory.Mappers
import tournament.events.auth.business.manager.user.ClaimManager

@Factory
class BusinessMapperFactory {

    @Singleton
    fun authenticationTokenMapper() = Mappers.getMapper(AuthenticationTokenMapper::class.java)

    @Singleton
    fun authorizationCodeMapper() = Mappers.getMapper(AuthorizationCodeMapper::class.java)

    @Singleton
    fun authorizeAttemptMapper() = Mappers.getMapper(AuthorizeAttemptMapper::class.java)

    @Singleton
    fun collectedUserInfoMapper(
        claimManager: ClaimManager,
        claimValueMapper: ClaimValueMapper
    ): CollectedUserInfoMapper {
        return Mappers.getMapper(CollectedUserInfoMapper::class.java).also {
            it.claimManager = claimManager
            it.claimValueMapper = claimValueMapper
        }
    }

    @Singleton
    fun collectedUserInfoUpdateMapper(
        claimValueMapper: ClaimValueMapper
    ): CollectedUserInfoUpdateMapper {
        return Mappers.getMapper(CollectedUserInfoUpdateMapper::class.java).also {
            it.claimValueMapper = claimValueMapper
        }
    }

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
