package no.nav.pensjon.kalkulator.simulering

class SimuleringException(
    message: String? = null,
    cause: Throwable? = null,
    val error: SimuleringError? = null,
    val status: SimuleringStatus? = null,
) : RuntimeException(message, cause)

data class SimuleringError(
    val status: String,
    val message: String
)

enum class SimuleringStatus {
    AFP_IKKE_I_VILKAARSPROEVING
}
