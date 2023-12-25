package tournament.events.auth.business.model.openid

/**
 * Enumaration of scopes defined in the OpenId specifications.
 *
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims">Scope claims</a>
 */
enum class OpenIdScope {
    OPEN_ID,
    PROFILE,
    EMAIL,
    ADDRESS,
    PHONE
}
