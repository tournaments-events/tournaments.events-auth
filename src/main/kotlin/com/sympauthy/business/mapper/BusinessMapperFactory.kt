package com.sympauthy.business.mapper

import com.sympauthy.business.manager.ClaimManager
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.mapstruct.factory.Mappers

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
    ): CollectedClaimMapper {
        return Mappers.getMapper(CollectedClaimMapper::class.java).also {
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
    fun encodedAuthenticationTokenMapper() = Mappers.getMapper(com.sympauthy.business.mapper.EncodedAuthenticationTokenMapper::class.java)

    @Singleton
    fun providerUserInfoMapper() = Mappers.getMapper(ProviderUserInfoMapper::class.java)

    @Singleton
    fun indexedCryptoKeysMapper() = Mappers.getMapper(IndexedCryptoKeysMapper::class.java)

    @Singleton
    fun userMapper() = Mappers.getMapper(com.sympauthy.business.mapper.UserMapper::class.java)
}
