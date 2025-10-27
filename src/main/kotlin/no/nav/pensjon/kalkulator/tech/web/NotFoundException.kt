package no.nav.pensjon.kalkulator.tech.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
