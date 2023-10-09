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
    FSS_GATEWAY("Fagsystemsone-gateway", "FSS", "Tilgang til Fagsystemsonen", GatewayUsage.INTERNAL),
    GANDALF_STS("Gandalf Security Token Service", "STS", "Tokenveksling til SAML", GatewayUsage.INTERNAL),
    ID_PORTEN("ID-porten", "IDP", "Token-utsteder", GatewayUsage.NONE),
    MICROSOFT_ENTRA_ID("Microsoft Entra ID", "MEID", "OAuth2 configuration data", GatewayUsage.NONE),
    NORSK_PENSJON("Norsk Pensjon", "NP", "Private pensjonsavtaler", GatewayUsage.INTERNAL),
    OAUTH2_TOKEN("OAuth2 token", "OA2", "OAuth2 access token", GatewayUsage.NONE),
    PENSJON_REGLER("Pensjon-regler", "PReg", "Pensjonsregler", GatewayUsage.INTERNAL),
    PENSJONSFAGLIG_KJERNE("Pensjonsfaglig kjerne", "PEN", "Simulering, pensjonsdata", GatewayUsage.NONE),
    PENSJONSOPPTJENING("Pensjonsopptjening", "POPP", "Pensjonsopptjeningsdata", GatewayUsage.NONE),
    PERSONDATALOESNINGEN("Persondataløsningen", "PDL", "Persondata", GatewayUsage.NONE),
    TJENESTEPENSJON("Tjenestepensjon", "TP", "Tjenestepensjonsforhold", GatewayUsage.NONE);

    companion object {
        val servicesAccessibleViaProxy = EgressService.values().filter { it.gatewayUsage == GatewayUsage.INTERNAL }
    }
}
