package no.nav.pensjon.kalkulator.tech.security.egress.config

/**
 * Specifies the services that is accessed by pensjonskalkulator-backend, and their characteristics.
 */
enum class EgressService(
    val description: String,
    val shortName: String,
    val purpose: String,
    val gatewayUsage: GatewayUsage
) {
    FSS_GATEWAY("Fagsystemsone-gateway", "UNT", "SOAP UsernameToken", GatewayUsage.INTERNAL),
    GANDALF_STS("Gandalf Security Token Service", "STS", "Tokenveksling til SAML", GatewayUsage.INTERNAL),
    MICROSOFT_ENTRA_ID("Microsoft Entra ID", "MEID", "OAuth2 configuration data", GatewayUsage.NONE),
    NORSK_PENSJON("Norsk Pensjon", "NP", "Private pensjonsavtaler", GatewayUsage.INTERNAL),
    OAUTH2_TOKEN("OAuth2 token", "OA2", "OAuth2 access token", GatewayUsage.NONE),
    PENSJON_REGLER("Pensjon-regler", "PReg", "Pensjonsregler", GatewayUsage.INTERNAL),
    PENSJONSFAGLIG_KJERNE("Pensjonsfaglig kjerne", "PEN", "Simulering, pensjonsdata", GatewayUsage.INTERNAL),
    PENSJONSOPPTJENING("Pensjonsopptjening", "POPP", "Pensjonsopptjeningsdata", GatewayUsage.INTERNAL),
    PERSONDATALOESNINGEN("Persondataløsningen", "PDL", "Persondata", GatewayUsage.NONE),
    TJENESTEPENSJON("Tjenestepensjon", "TP", "Tjenestepensjonsforhold", GatewayUsage.NONE);

    companion object {
        val servicesAccessibleViaProxy = EgressService.values().filter { it.gatewayUsage == GatewayUsage.INTERNAL }
    }
}
