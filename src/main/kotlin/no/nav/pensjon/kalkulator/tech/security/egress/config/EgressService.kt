package no.nav.pensjon.kalkulator.tech.security.egress.config

import java.util.Arrays.stream
import java.util.stream.Collectors.toUnmodifiableList

/**
 * Specifies the services that is accessed by pensjonskalkulator-backend, and their characteristics.
 */
enum class EgressService(val isAccessibleViaProxy: Boolean) {

    PENSJON_REGLER(true);

    companion object {
        val servicesAccessibleViaProxy: List<EgressService> =
            stream(EgressService.values()).filter { it.isAccessibleViaProxy }.collect(toUnmodifiableList())
    }
}
