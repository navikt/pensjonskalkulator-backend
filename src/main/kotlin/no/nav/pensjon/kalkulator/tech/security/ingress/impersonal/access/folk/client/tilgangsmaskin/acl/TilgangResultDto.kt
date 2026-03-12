package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl

sealed class TilgangResultDto {
    data object Innvilget : TilgangResultDto()
    data class Avvist(val detail: ProblemDetailResponseDto) : TilgangResultDto()
}