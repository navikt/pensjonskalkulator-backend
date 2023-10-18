package no.nav.pensjon.kalkulator.opptjening.api.map

import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.api.dto.InntektDto

object InntektMapper {
    fun toDto(inntekt: Inntekt) = InntektDto(inntekt.beloep.toInt(), inntekt.aar)
}
