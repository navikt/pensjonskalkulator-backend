package no.nav.pensjon.kalkulator.tech.security.egress.token.validation

import java.time.LocalDateTime

interface TimeProvider {
    fun time(): LocalDateTime
}
