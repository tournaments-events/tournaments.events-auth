package tournament.events.auth.business.mapper

import io.micronaut.serde.ObjectMapper
import jakarta.inject.Inject
import jakarta.inject.Singleton
import tournament.events.auth.business.model.user.claim.ClaimDataType
import java.io.IOException

@Singleton
class ClaimValueMapper(
    @Inject private val objectMapper: ObjectMapper
) {

    fun toBusiness(value: String?, claimType: ClaimDataType): Any? {
        if (value == null) {
            return null
        }
        return try {
            objectMapper.readValue(value, claimType.typeClass.java)
        } catch (e: IOException) {
            null
        }
    }

    fun toEntity(value: Any?): String? {
        return objectMapper.writeValueAsString(value)
    }
}
