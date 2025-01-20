package no.nav.pensjon.kalkulator.simulering

class SimuleringException(
    message: String? = null,
    cause: Throwable? = null,
    val error: SimuleringError? = null
) : RuntimeException(message, cause)

data class SimuleringError(
    val status: String,
    val message: String
)
