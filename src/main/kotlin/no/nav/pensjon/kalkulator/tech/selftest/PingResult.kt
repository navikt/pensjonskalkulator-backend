package no.nav.pensjon.kalkulator.tech.selftest

import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService

data class PingResult(
    val service: EgressService,
    val status: ServiceStatus,
    val endpoint: String,
    val message: String
)
