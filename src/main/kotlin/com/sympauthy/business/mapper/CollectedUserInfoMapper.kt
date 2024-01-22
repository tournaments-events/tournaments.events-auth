package com.sympauthy.business.mapper

import com.sympauthy.business.manager.user.ClaimManager
import com.sympauthy.business.mapper.config.ToBusinessMapperConfig
import com.sympauthy.business.model.user.CollectedUserInfo
import com.sympauthy.data.model.CollectedUserInfoEntity
import org.mapstruct.Mapper

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
