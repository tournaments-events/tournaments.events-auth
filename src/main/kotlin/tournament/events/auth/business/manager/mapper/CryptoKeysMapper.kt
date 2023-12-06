package tournament.events.auth.business.manager.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import tournament.events.auth.business.manager.mapper.config.BusinessMapperConfig
import tournament.events.auth.business.model.key.CryptoKeys
import tournament.events.auth.data.model.CryptoKeysEntity

@Mapper(
    config = BusinessMapperConfig::class
)
interface CryptoKeysMapper {

    fun toCryptoKeys(entity: CryptoKeysEntity): CryptoKeys

    @Mappings(
        Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    )
    fun toEntity(key: CryptoKeys): CryptoKeysEntity
}
