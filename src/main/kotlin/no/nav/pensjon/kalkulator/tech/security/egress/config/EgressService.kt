package no.nav.pensjon.kalkulator.tech.security.egress.config

/**
 * Specifies the services that is accessed by pensjonskalkulator-backend, and their characteristics.
 */
enum class EgressService(val description: String, val isAccessibleViaProxy: Boolean) {

    PENSJON_REGLER("Pensjonsregler", true),
    PERSONDATA("Persondata", true),
    PENSJONSOPPTJENING("Pensjonsopptjening", true);

    companion object {
        val servicesAccessibleViaProxy = EgressService.values().filter { it.isAccessibleViaProxy }
    }
}
