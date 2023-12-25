package tournament.events.auth.business.manager.user

import tournament.events.auth.business.model.provider.ProviderUserInfo
import tournament.events.auth.business.model.user.RawUserInfoBuilder
import tournament.events.auth.business.model.user.CollectedUserInfo
import tournament.events.auth.business.model.user.RawUserInfo
import tournament.events.auth.business.model.user.StandardClaim.*
import tournament.events.auth.util.nullIfBlank
import java.time.LocalDateTime
import java.util.*

/**
 * Merge the user info we collected with the ones provided by the third-party providers.
 *
 * - We apply the info collected by third-party providers (in [providerUserInfoList]) in chronological order.
 *   The chronological order is determined by the [ProviderUserInfo.updatedAt] field.
 *   The [ProviderUserInfo.changeDate] is used if the [ProviderUserInfo.updatedAt] field is null.
 *
 * - Then we apply the info we collected as a first-party that are stored either in [user] or in [collectedUserInfoEntity].
 *
 * - TODO: Finally, we filter the user info that will be returned according to the scope and the claims.
 */
internal class UserInfoMerger(
    // private val user: User,
    private val userId: UUID,
    private val collectedUserInfo: CollectedUserInfo? = null,
    private val providerUserInfoList: List<ProviderUserInfo> = emptyList()
) {

    fun merge(): RawUserInfo {
        return RawUserInfoBuilder(userId).apply {
            merge(this)
        }.build()
    }

    internal fun merge(builder: RawUserInfoBuilder) {
        providerUserInfoList.sortedBy(::getUpdatedAt)
            .fold(builder, ::apply)
        collectedUserInfo?.let { apply(builder, it) }
    }

    internal fun getUpdatedAt(providerUserInfo: ProviderUserInfo): LocalDateTime {
        return providerUserInfo.userInfo.updatedAt ?: providerUserInfo.changeDate
    }

    internal fun apply(builder: RawUserInfoBuilder, providerUserInfo: ProviderUserInfo): RawUserInfoBuilder {
        val info = providerUserInfo.userInfo

        info.name.nullIfBlank()?.let(builder::withName)
        info.givenName.nullIfBlank()?.let(builder::withGivenName)
        info.familyName.nullIfBlank()?.let(builder::withFamilyName)
        info.middleName.nullIfBlank()?.let(builder::withMiddleName)
        info.nickname.nullIfBlank()?.let(builder::withNickname)

        info.preferredUsername.nullIfBlank()?.let(builder::withPreferredUsername)
        info.profile.nullIfBlank()?.let(builder::withProfile)
        info.picture.nullIfBlank()?.let(builder::withPicture)
        info.website.nullIfBlank()?.let(builder::withWebsite)

        if (info.email?.isNotBlank() == true) {
            builder.withEmail(info.email, info.emailVerified)
        }

        info.gender.nullIfBlank()?.let(builder::withGender)
        info.birthDate?.let(builder::withBirthDate)

        info.zoneInfo.nullIfBlank()?.let(builder::withZoneInfo)
        info.locale.nullIfBlank()?.let(builder::withLocale)

        if (info.phoneNumber?.isNotBlank() == true) {
            builder.withPhoneNumber(info.phoneNumber, info.phoneNumberVerified)
        }
        getUpdatedAt(providerUserInfo).let(builder::withUpdateAt)
        return builder
    }

    internal fun apply(builder: RawUserInfoBuilder, collectedUserInfo: CollectedUserInfo): RawUserInfoBuilder {
        val info = collectedUserInfo.info
        val collectedInfo = collectedUserInfo.collectedInfo

        if (collectedInfo.contains(NAME)) {
            builder.withName(info.name)
        }
        if (collectedInfo.contains(GIVEN_NAME)) {
            builder.withGivenName(info.givenName)
        }
        if (collectedInfo.contains(FAMILY_NAME)) {
            builder.withFamilyName(info.familyName)
        }
        if (collectedInfo.contains(MIDDLE_NAME)) {
            builder.withMiddleName(info.middleName)
        }
        if (collectedInfo.contains(NICKNAME)) {
            builder.withNickname(info.nickname)
        }

        if (collectedInfo.contains(PREFERRED_USERNAME)) {
            builder.withPreferredUsername(info.preferredUsername)
        }
        if (collectedInfo.contains(PROFILE)) {
            builder.withProfile(info.profile)
        }
        if (collectedInfo.contains(PICTURE)) {
            builder.withPicture(info.picture)
        }
        if (collectedInfo.contains(WEBSITE)) {
            builder.withWebsite(info.website)
        }

        if (collectedInfo.contains(EMAIL)) {
            builder.withEmail(info.email, info.emailVerified)
        }

        if (collectedInfo.contains(GENDER)) {
            builder.withGender(info.gender)
        }
        if (collectedInfo.contains(BIRTH_DATE)) {
            builder.withBirthDate(info.birthDate)
        }

        if (collectedInfo.contains(ZONE_INFO)) {
            builder.withZoneInfo(info.zoneInfo)
        }
        if (collectedInfo.contains(LOCALE)) {
            builder.withLocale(info.locale)
        }

        if (collectedInfo.contains(PHONE_NUMBER)) {
            builder.withPhoneNumber(info.phoneNumber, info.phoneNumberVerified)
        }
        info.updatedAt?.let(builder::withUpdateAt)
        return builder
    }
}
