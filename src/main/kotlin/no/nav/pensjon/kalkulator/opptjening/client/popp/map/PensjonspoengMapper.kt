package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengResponseDto

object PensjonspoengMapper {

    fun fromDto(dto: PensjonspoengResponseDto): List<AarligOpptjening> =
        dto.pensjonspoeng.orEmpty().mapNotNull(::fromDto)

    private fun fromDto(dto: PensjonspoengDto): AarligOpptjening? {
        val ar = dto.ar ?: return null
        val pensjonspoeng = dto.poeng ?: return null
        val pensjonspoengType = dto.pensjonspoengType ?: return null
        val pensjonsgivendeInntekt = dto.inntekt?.belop?.toInt() ?: return null

        return AarligOpptjening(
            aar = ar,
            pensjonsgivendeInntekt = pensjonsgivendeInntekt,
            pensjonspoeng = pensjonspoeng,
            pensjonspoengType = pensjonspoengType,
            maksimalUfoeregrad = dto.maxUforegrad,
            omsorgspoeng = dto.omsorg?.ar
        )
    }
}
