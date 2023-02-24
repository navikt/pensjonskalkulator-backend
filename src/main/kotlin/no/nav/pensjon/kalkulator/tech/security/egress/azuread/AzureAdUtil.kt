package no.nav.pensjon.kalkulator.tech.security.egress.azuread

import no.nav.pensjon.kalkulator.tech.web.UriUtil.formatAsUri

object AzureAdUtil {

    private const val APPLICATION_URI_SCHEME = "api"

    // docs.microsoft.com/en-us/azure/active-directory/develop/v2-permissions-and-consent#the-default-scope
    private const val AZURE_AD_DEFAULT_SCOPE = ".default"

    /**
     * Gets the default application scope in the form "api://<cluster>.<namespace>.<app-name>/.default",
     * ref. https://doc.nais.io/security/auth/azure-ad/concepts/#scopes
     */
    fun getDefaultScope(fullyQualifiedApplicationName: String): String =
        formatAsApplicationIdUri(fullyQualifiedApplicationName) + "/" + AZURE_AD_DEFAULT_SCOPE

    /**
     * Gets the application ID URI for a fully qualified application name of the form "<cluster>:<namespace>:<app-name>".
     * The URI is of the form "api://<cluster>.<namespace>.<app-name>".
     * The fully qualified application name is referred to as 'application display name' in Azure AD.
     */
    private fun formatAsApplicationIdUri(fullyQualifiedApplicationName: String): String =
        formatAsUri(
            APPLICATION_URI_SCHEME,
            fullyQualifiedApplicationName.replace(":", "."),
            ""
        )
}
