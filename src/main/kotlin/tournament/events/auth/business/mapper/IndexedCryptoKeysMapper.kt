package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import tournament.events.auth.business.mapper.config.ToBusinessMapperConfig
import tournament.events.auth.business.model.key.CryptoKeys
import tournament.events.auth.data.model.IndexedCryptoKeysEntity

@Mapper(
    config = ToBusinessMapperConfig::class
)
interface IndexedCryptoKeysMapper {

    fun toCryptoKeys(entity: IndexedCryptoKeysEntity): CryptoKeys

    @Mappings(
        Mapping(target = "index", ignore = true),
        Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    )
    fun toEntity(keys: CryptoKeys): IndexedCryptoKeysEntity
}
