package tournament.events.auth.business.mapper

import org.mapstruct.Mapper
import tournament.events.auth.business.manager.user.ClaimManager
import tournament.events.auth.business.mapper.config.ToBusinessMapperConfig
import tournament.events.auth.business.model.user.CollectedUserInfo
import tournament.events.auth.data.model.CollectedUserInfoEntity

@Mapper(
    config = ToBusinessMapperConfig::class
)
abstract class CollectedUserInfoMapper {

    lateinit var claimManager: ClaimManager

    lateinit var claimValueMapper: ClaimValueMapper

    /**
     * Convert a collected user info entity into a business object.
     *
     * Return null if the user info cannot be converted back. For example, if the data type of custom claim
     * has changed and the data cannot be deserialized anymore.
     */
    fun toCollectedUserInfo(entity: CollectedUserInfoEntity): CollectedUserInfo? {
        val claim = claimManager.findById(entity.claim) ?: return null
        val value = if (entity.value != null) {
            claimValueMapper.toBusiness(entity.value, claim.dataType) ?: return null
        } else null
        return CollectedUserInfo(
            userId = entity.userId,
            claim = claim,
            value = value,
            verified = entity.verified,
            collectionDate = entity.collectionDate
        )
    }
}
