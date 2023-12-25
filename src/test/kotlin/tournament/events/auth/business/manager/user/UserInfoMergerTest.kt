package tournament.events.auth.business.manager.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import tournament.events.auth.business.model.provider.ProviderUserInfo
import tournament.events.auth.business.model.user.CollectedUserInfo
import tournament.events.auth.business.model.user.RawUserInfo
import tournament.events.auth.business.model.user.RawUserInfoBuilder
import tournament.events.auth.business.model.user.StandardClaim
import java.time.LocalDate
import java.time.LocalDateTime.now
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserInfoMergerTest {

    @Test
    fun `merge - Providers in chronological order then collected`() {
        val userId = UUID.randomUUID()
        val builder = mock<RawUserInfoBuilder>()
        val collectedUserInfo = mock<CollectedUserInfo>()
        val providerUserInfo1 = mock<ProviderUserInfo>()
        val providerUserInfo2 = mock<ProviderUserInfo>()

        val merger = spy(UserInfoMerger(
            userId = userId,
            collectedUserInfo = collectedUserInfo,
            providerUserInfoList = listOf(providerUserInfo1, providerUserInfo2)
        ))
        doReturn(now()).whenever(merger).getUpdatedAt(providerUserInfo1)
        doReturn(now().minusDays(1)).whenever(merger).getUpdatedAt(providerUserInfo2)
        doReturn(builder).whenever(merger).apply(builder, providerUserInfo1)
        doReturn(builder).whenever(merger).apply(builder, providerUserInfo2)
        doReturn(builder).whenever(merger).apply(builder, collectedUserInfo)

        merger.merge(builder)

        inOrder(merger) {
            merger.apply(builder, providerUserInfo2)
            merger.apply(builder, providerUserInfo1)
            merger.apply(builder, collectedUserInfo)
        }
    }

    @Test
    fun `apply - Copy all fields if everything is provided`() {
        val userId = UUID.randomUUID()
        val providerRawInfo = fullRawInfo(userId)
        val providerUserInfo = mock<ProviderUserInfo> {
            on { it.userInfo } doReturn providerRawInfo
        }

        val merger = UserInfoMerger(mock(), mock(), mock())
        val rawInfo = merger.apply(
            RawUserInfoBuilder(userId),
            providerUserInfo
        ).build()

        assertEquals(providerRawInfo, rawInfo)
    }

    @Test
    fun `apply - Copy all fields if everything is collected`() {
        val userId = UUID.randomUUID()
        val collectedRawInfo = fullRawInfo(userId)
        val collectedUserInfo = CollectedUserInfo(
            userId = userId,
            collectedInfo = StandardClaim.values().toList(),
            info = collectedRawInfo
        )

        val merger = UserInfoMerger(mock(), mock(), mock())
        val rawInfo = merger.apply(
            RawUserInfoBuilder(userId),
            collectedUserInfo
        ).build()

        assertEquals(collectedRawInfo, rawInfo)
    }

    private fun fullRawInfo(userId: UUID) = RawUserInfo(
        subject = userId.toString(),

        name = "name",
        givenName = "givenName",
        familyName = "familyName",
        middleName = "middleName",
        nickname = "nickname",

        preferredUsername = "preferredUsername",
        profile = "profile",
        picture = "picture",
        website = "website",

        email = "email",
        emailVerified = true,

        gender = "gender",
        birthDate = LocalDate.now(),

        zoneInfo = "zoneInfo",
        locale = "locale",

        phoneNumber = "phoneNumber",
        phoneNumberVerified = true
    )
}
