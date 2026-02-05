package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.dto

data class ProblemDetailResponseDto(
    val type: String = "",
    val title: AvvisningsKodeDto = AvvisningsKodeDto.AVVIST_UKJENT_BOSTED,
    val status: Int = 0,
    val instance: String = "",
    val brukerIdent: String = "",
    val navIdent: String = "",
    val begrunnelse: String = "",
    val traceId: String = "",
    val kanOverstyres: Boolean = false
)
