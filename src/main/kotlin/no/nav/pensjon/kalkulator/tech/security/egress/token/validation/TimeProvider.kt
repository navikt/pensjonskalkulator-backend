package no.nav.pensjon.kalkulator.tech.security.egress.token.validation

import java.time.LocalDateTime

fun interface TimeProvider {
    fun time(): LocalDateTime
}
