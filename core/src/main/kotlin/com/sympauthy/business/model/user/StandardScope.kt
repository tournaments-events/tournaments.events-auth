package com.sympauthy.business.model.user

/**
 * Enumeration of scope, defined in the OpenID specification, that are supported by this application.
 *
 * TODO: Add support for custom scope
 * TODO: Allow to disable standard scopes from config
 *
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims">Scope</a>
 */
enum class StandardScope(
    val id: String
) {
    OPENID(StandardScopeId.OPENID),
    PROFILE(StandardScopeId.PROFILE),
    EMAIL(StandardScopeId.EMAIL),
    ADDRESS(StandardScopeId.ADDRESS),
    PHONE(StandardScopeId.PHONE);
}

object StandardScopeId {
    const val OPENID = "openid"
    const val PROFILE = "profile"
    const val EMAIL = "email"
    const val ADDRESS = "address"
    const val PHONE = "phone"
}
