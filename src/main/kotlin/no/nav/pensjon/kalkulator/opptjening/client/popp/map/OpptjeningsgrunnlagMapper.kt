package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.InntektDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagResponseDto
import java.math.BigDecimal

object OpptjeningsgrunnlagMapper {

    fun fromDto(dto: OpptjeningsgrunnlagResponseDto): Opptjeningsgrunnlag =
        Opptjeningsgrunnlag(fromDto(dto.opptjeningsGrunnlag.inntektListe))

    private fun fromDto(dto: List<InntektDto>): List<Inntekt> =
        dto.map { Inntekt(Opptjeningstype.forCode(it.inntektType), it.inntektAr, BigDecimal(it.belop)) }
}
