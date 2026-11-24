package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.HarLivsvarigOffentligAfpDtoV1

object HarLivsvarigOffentligAfpV1DtoMapper {
    fun toDto(harLivsvarigOffentligAfp: Boolean) = HarLivsvarigOffentligAfpDtoV1(harLivsvarigOffentligAfp)
}