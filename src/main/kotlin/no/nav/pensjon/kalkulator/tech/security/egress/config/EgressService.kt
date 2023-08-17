package no.nav.pensjon.kalkulator.tech.security.egress.config

/**
 * Specifies the services that is accessed by pensjonskalkulator-backend, and their characteristics.
 */
enum class EgressService(val description: String, val gatewayUsage: GatewayUsage) {

    PENSJONSAVTALER("Norsk Pensjon", GatewayUsage.INTERNAL),
    PENSJON_REGLER("Pensjonsregler", GatewayUsage.INTERNAL),
    PERSONDATA("Persondata", GatewayUsage.NONE),
    PENSJONSOPPTJENING("Pensjonsopptjening", GatewayUsage.INTERNAL),
    SAML_TOKEN("Gandalf STS", GatewayUsage.INTERNAL),
    SIMULERING("Simulering", GatewayUsage.INTERNAL),
    TJENESTEPENSJONSFORHOLD("Tjenestepensjonsforhold", GatewayUsage.INTERNAL),
    USERNAME_TOKEN("SOAP UsernameToken", GatewayUsage.INTERNAL);

    companion object {
        val servicesAccessibleViaProxy = EgressService.values().filter { it.gatewayUsage == GatewayUsage.INTERNAL }
    }
}
