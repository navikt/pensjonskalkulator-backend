package no.nav.pensjon.kalkulator.tech.security.egress.config

/**
 * Specifies the services that is accessed by pensjonskalkulator-backend, and their characteristics.
 */
enum class EgressService(val description: String, val gatewayUsage: GatewayUsage) {

    OAUTH2_CONFIGURATION("OAuth2 configuration", GatewayUsage.NONE),
    OAUTH2_TOKEN("OAuth2 token", GatewayUsage.NONE),
    PENSJONSAVTALER("Norsk Pensjon", GatewayUsage.INTERNAL),
    PENSJON_REGLER("Pensjonsregler", GatewayUsage.INTERNAL),
    PERSONDATA("Persondata", GatewayUsage.NONE),
    PENSJONSOPPTJENING("Pensjonsopptjening", GatewayUsage.INTERNAL),
    SAML_TOKEN("Gandalf STS", GatewayUsage.INTERNAL),
    TJENESTEPENSJONSFORHOLD("Tjenestepensjonsforhold", GatewayUsage.NONE),
    USERNAME_TOKEN("SOAP UsernameToken", GatewayUsage.INTERNAL),
    PENSJONSFAGLIG_KJERNE("PEN", GatewayUsage.INTERNAL);

    companion object {
        val servicesAccessibleViaProxy = EgressService.values().filter { it.gatewayUsage == GatewayUsage.INTERNAL }
    }
}
