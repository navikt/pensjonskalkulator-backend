package no.nav.pensjon.kalkulator.tech.security.egress.config

import no.nav.pensjon.kalkulator.tech.security.egress.AuthType

/**
 * Specifies the services that is accessed by pensjonskalkulator-backend, and their characteristics.
 */
enum class EgressService(
    val description: String,
    val shortName: String,
    val purpose: String,
    val gatewayUsage: GatewayUsage = GatewayUsage.NONE,
    val authType: AuthType = AuthType.MACHINE_INSIDE_NAV
) {
    FSS_GATEWAY("Fagsystemsone-gateway", "FSS", "Tilgang til Fagsystemsonen", GatewayUsage.INTERNAL),
    GANDALF_STS("Gandalf Security Token Service", "STS", "Tokenveksling til SAML", GatewayUsage.INTERNAL),
    ID_PORTEN("ID-porten", "IDP", "Token-utsteder"),
    MICROSOFT_ENTRA_ID("Microsoft Entra ID", "MEID", "OAuth2 configuration data"),
    NORSK_PENSJON("Norsk Pensjon", "NP", "Private pensjonsavtaler", GatewayUsage.INTERNAL),
    OAUTH2_TOKEN("OAuth2 token", "OA2", "OAuth2 access token"),
    PENSJON_REGLER("Pensjon-regler", "PReg", "Pensjonsregler", GatewayUsage.INTERNAL),
    PENSJON_REPRESENTASJON(
        description = "Pensjon-representasjon",
        shortName = "Rep",
        purpose = "Representasjonsforhold (fullmakt m.m.)",
        authType = AuthType.PERSON_SELF // personal token required
    ),
    PENSJONSFAGLIG_KJERNE("Pensjonsfaglig kjerne", "PEN", "Simulering, pensjonsdata"),
    PENSJONSOPPTJENING("Pensjonsopptjening", "POPP", "Pensjonsopptjeningsdata"),
    PERSONDATALOESNINGEN("Persondataløsningen", "PDL", "Persondata"),
    SKJERMEDE_PERSONER("Skjermede personer", "SP", "Skjerming"),
    PENSJONSSIMULATOR(
        description = "Pensjonssimulator",
        shortName = "PS",
        purpose = "Simulering av alderspensjon",
        authType = AuthType.MACHINE_OUTSIDE_NAV // regarded as an external service when testing
    ),
    TJENESTEPENSJON("Tjenestepensjon", "TP", "Tjenestepensjonsforhold"),
    OMSTILLINGSSTOENAD("Omstillingsstoenad", "OS", "Finne om bruker mottar omstillingsstoenad");

    companion object {
        val servicesAccessibleViaProxy = entries.filter { it.gatewayUsage == GatewayUsage.INTERNAL }
    }
}
