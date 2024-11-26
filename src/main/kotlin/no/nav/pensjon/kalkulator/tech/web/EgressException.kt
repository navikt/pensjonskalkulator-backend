package no.nav.pensjon.kalkulator.tech.web

import no.nav.pensjon.kalkulator.simulering.api.dto.AnonymSimuleringErrorV1
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

class EgressException(
    message: String,
    cause: Throwable? = null,
    val statusCode: HttpStatusCode? = null,
    var errorObj: AnonymSimuleringErrorV1? = null
) : RuntimeException(message, cause) {
    val isClientError: Boolean = statusCode?.is4xxClientError ?: false
    val isConflict: Boolean = statusCode == HttpStatus.CONFLICT
}
