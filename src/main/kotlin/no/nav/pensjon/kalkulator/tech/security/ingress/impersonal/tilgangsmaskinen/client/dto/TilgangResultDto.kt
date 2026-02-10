package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.dto

sealed class TilgangResultDto {
    data object Innvilget : TilgangResultDto()
    data class Avvist(val detail: ProblemDetailResponseDto) : TilgangResultDto()
}