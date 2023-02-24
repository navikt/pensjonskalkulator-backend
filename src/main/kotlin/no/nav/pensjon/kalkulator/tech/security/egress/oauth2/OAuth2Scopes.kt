package no.nav.pensjon.kalkulator.tech.security.egress.oauth2

object OAuth2Scopes {

    /**
     * OpenID - for obtaining ID token (application intends to use OIDC to verify user's identity)
     * https://openid.net/specs/openid-connect-core-1_0.html#Introduction
     */
    const val OPENID = "openid"

    /**
     * OIDC profile - for obtaining user's name
     * https://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims
     */
    const val PROFILE = "profile"

    /**
     * OIDC offline access - for obtaining refresh token
     * https://openid.net/specs/openid-connect-core-1_0.html#OfflineAccess
     */
    const val OFFLINE_ACCESS = "offline_access"

    /**
     * Azure AD default scope
     * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-permissions-and-consent#the-default-scope
     * Note: Cannot be combined with other scopes
     */
    const val AAD_DEFAULT = ".default"
}
