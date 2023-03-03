package no.nav.pensjon.kalkulator.opptjening.client.regler.map

import no.nav.pensjon.kalkulator.opptjening.Opptjening
import no.nav.pensjon.kalkulator.opptjening.Opptjeningshistorikk
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningshistorikkSpec
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.*
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

object OpptjeningMapper {

    fun toDto(spec: OpptjeningshistorikkSpec) =
        OpptjeningRequestDto(spec.opptjeninger.map {
            AldersopptjeningDto(
                PensjonsgivendeInntektDto(
                    it.aar,
                    it.pensjonsgivendeInntekt,
                    OpptjeningstypeDto(it.opptjeningstype.code)
                ), toDto(spec.foedselsdato)
            )
        })

    fun fromDto(dto: OpptjeningResponseDto): Opptjeningshistorikk =
        Opptjeningshistorikk(
            dto.personOpptjeningsgrunnlagListe.associateBy(
                { it.opptjening.ar },
                { fromDto(it.opptjening) })
        )

    private fun toDto(date: LocalDate): Date = Date.from(date.atTime(12, 0).toInstant(ZoneOffset.ofHours(2)))

    private fun fromDto(dto: OpptjeningsdetaljerDto): Opptjening = Opptjening(dto.pia, dto.pp)
}
