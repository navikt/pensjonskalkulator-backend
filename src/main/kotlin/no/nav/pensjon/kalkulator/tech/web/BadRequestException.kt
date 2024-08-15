package no.nav.pensjon.kalkulator.tech.web

class BadRequestException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
