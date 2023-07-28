package no.nav.pensjon.kalkulator.tech.web

class EgressException(
    val isClientError: Boolean,
    message: String,
    cause: Throwable?
) : RuntimeException(message, cause) {
    constructor(message: String, cause: Throwable?) : this(false, message, cause)

    constructor(isClientError: Boolean, message: String) : this(isClientError, message, null)
}
