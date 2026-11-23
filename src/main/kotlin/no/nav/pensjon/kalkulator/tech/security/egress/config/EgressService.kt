package no.nav.pensjon.kalkulator.tech.security.egress.config

import no.nav.pensjon.kalkulator.tech.security.egress.AuthType

/**
 * Specifies the services that are accessed by pensjonskalkulator-backend, and their characteristics.
 */
enum class EgressService(
    val description: String,
    val shortName: String,
    val purpose: String,
    val authType: AuthType
) {
    // Nav-interne tjenester:
    OPPTJENING_SELVBETJENING(
        description = "Prosesserte pensjonsopptjeningsdata",
        shortName = "OS",
        purpose = "Hente merknader relatert til pensjonsopptjening",
        authType = AuthType.PERSONAL_NOT_TOKEN_X
    ),
    PENSJON_PERSONDATA(
        description = "Pensjonsrelaterte persondata",
        shortName = "PPD",
        purpose = "Hente persondata relatert til pensjon",
        authType = AuthType.PERSONAL_NOT_TOKEN_X
    ),
    PENSJON_PID_ENCRYPTION(
        description = "PID-kryptering for pensjon",
        shortName = "PPE",
        purpose = "Kryptering av person-ID for pensjonsapplikasjoner",
        authType = AuthType.MACHINE_INSIDE_NAV
    ),
    PENSJON_REPRESENTASJON(
        description = "Pensjon-representasjon",
        shortName = "Rep",
        purpose = "Representasjonsforhold (fullmakt m.m.)",
        authType = AuthType.PERSON_SELF // tokenet må inneholde fullmektigens person-ID
    ),
    PENSJONSFAGLIG_KJERNE(
        description = "Pensjonsfaglig kjerne",
        shortName = "PEN",
        purpose = "Simulering, pensjonsdata",
        authType = AuthType.MACHINE_INSIDE_NAV
    ),
    PENSJONSOPPTJENING(
        description = "Pensjonsopptjening",
        shortName = "POPP",
        purpose = "Pensjonsopptjeningsdata",
        authType = AuthType.PERSONAL_NOT_TOKEN_X
    ),
    PERSONDATALOESNINGEN(
        description = "Persondataløsningen",
        shortName = "PDL",
        purpose = "Persondata",
        authType = AuthType.PERSONAL_NOT_TOKEN_X
    ),
    PENSJONSSIMULATOR(
        description = "Pensjonssimulator",
        shortName = "PS",
        purpose = "Simulering av alderspensjon",
        authType = AuthType.MACHINE_INSIDE_NAV
    ),
    TJENESTEPENSJON(
        description = "Tjenestepensjon",
        shortName = "TP",
        purpose = "Tjenestepensjonsforhold",
        authType = AuthType.PERSONAL_NOT_TOKEN_X
    ),
    UTBETALING_DATA(
        description = "Oekonomi utbetalingsdata",
        shortName = "Sokos",
        purpose = "Finne siste maaneds utbetaling",
        authType = AuthType.PERSONAL_NOT_TOKEN_X
    ),
    OMSTILLINGSSTOENAD(
        description = "Omstillingsstoenad",
        shortName = "OS",
        purpose = "Finne om bruker mottar omstillingsstoenad",
        authType = AuthType.MACHINE_INSIDE_NAV
    ),
    NAV_ANSATT(
        description = "Nav-ansatt",
        shortName = "NA",
        purpose = "Info om Nav-ansatt",
        authType = AuthType.NAV_ANSATT
    ),
    TILGANGSMASKINEN(
        description = "Tilgangsmaskinen",
        shortName = "Tilgangsmaskinen",
        purpose = "Sjekk av veileders tilgang til person",
        authType = AuthType.NAV_ANSATT
    ),
    SKRIBENTEN(
        description = "Skribenten",
        shortName = "Skribenten",
        purpose = "Lagring av simulering",
        authType = AuthType.NAV_ANSATT
    ),

    // Eksterne tjenester:
    NORSK_PENSJON_REST(
        description = "Norsk Pensjon",
        shortName = "NP",
        purpose = "Private pensjonsavtaler",
        authType = AuthType.MUTUAL_TLS
    ),
    AKSIO(
        description = "Aksio",
        shortName = "aksio",
        purpose = "Hente løpende livsvarig offentlig AFP fra Aksio",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),
    GABLER(
        description = "Gabler",
        shortName = "gabler",
        purpose = "Hente løpende livsvarig offentlig AFP fra Gabler",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),
    GABLER2(
        description = "Gabler2",
        shortName = "gabler2",
        purpose = "Hente løpende livsvarig offentlig AFP fra Gabler2",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),
    KLP(
        description = "KLP",
        shortName = "klp",
        purpose = "Hente løpende livsvarig offentlig AFP fra KLP",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),
    OPF(
        description = "OPF",
        shortName = "opf",
        purpose = "Hente løpende livsvarig offentlig AFP fra OPF",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),
    SPK(
        description = "SPK",
        shortName = "spk",
        purpose = "Hente løpende livsvarig offentlig AFP fra KLP",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),
    STOREBRAND_LIVSFORSIKRING(
        description = "Storebrand Livsforsikring",
        shortName = "storebrand",
        purpose = "Hente løpende livsvarig offentlig AFP fra Storebrand Livsforsikring",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),
    STOREBRAND_PENSJONSTJENESTER(
        description = "Storebrand Pensjonstjenester",
        shortName = "storebrand_pen",
        purpose = "Hente løpende livsvarig offentlig AFP fra Storebrand Pensjonstjenester",
        authType = AuthType.MACHINE_OUTSIDE_NAV
    ),

    // Entra-konfigurasjonen her gjelder self-test:
    MICROSOFT_ENTRA_ID(
        description = "Microsoft Entra ID",
        shortName = "MEID",
        purpose = "OAuth2 configuration data",
        authType = AuthType.NONE
    ),

    // Felles-konfigurasjon for tjenester som leverer egress-token:
    OAUTH2_TOKEN(
        description = "OAuth2 token",
        shortName = "OA2",
        purpose = "OAuth2 access token",
        authType = AuthType.NONE
    )
}