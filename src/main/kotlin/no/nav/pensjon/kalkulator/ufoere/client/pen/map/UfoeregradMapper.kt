package no.nav.pensjon.kalkulator.ufoere.client.pen.map

import no.nav.pensjon.kalkulator.ufoere.Ufoeregrad

object UfoeregradMapper {
    fun fromDto(dto: UfoeregradPenDto): Ufoeregrad = Ufoeregrad(dto.uforegrad ?: 0)
}