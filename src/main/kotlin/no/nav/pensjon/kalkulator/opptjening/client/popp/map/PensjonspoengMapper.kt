package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengResponseDto

object PensjonspoengMapper {

    fun fromDto(dto: PensjonspoengResponseDto): List<AarligOpptjening> =
        dto.pensjonspoeng.orEmpty().mapNotNull(::fromDto)

    private fun fromDto(dto: PensjonspoengDto): AarligOpptjening? =
        dto.ar?.let {
            AarligOpptjening(
                aar = it,
                pensjonsgivendeInntekt = dto.inntekt?.belop?.toInt() ?: 0,
                pensjonspoeng = dto.poeng ?: 0.0,
                pensjonspoengType = dto.pensjonspoengType ?: "",
                maksimalUfoeregrad = dto.maxUforegrad,
                omsorgspoeng = dto.omsorg?.ar,
                beholdning = 0 // not available here
            )
        }
}