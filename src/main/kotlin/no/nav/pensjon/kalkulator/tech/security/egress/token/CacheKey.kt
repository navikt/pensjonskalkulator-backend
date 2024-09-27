package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.person.Pid

data class CacheKey(
    val scope: String,
    val pid: Pid
)
