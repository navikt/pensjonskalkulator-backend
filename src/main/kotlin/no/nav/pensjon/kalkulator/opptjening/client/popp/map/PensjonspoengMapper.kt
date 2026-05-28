package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import no.nav.pensjon.kalkulator.opptjening.Pensjonspoeng
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengResponseDto

object PensjonspoengMapper {

    fun fromDto(dto: PensjonspoengResponseDto): List<Pensjonspoeng> =
        dto.pensjonspoeng.orEmpty().mapNotNull(::fromDto)

    private fun fromDto(dto: PensjonspoengDto): Pensjonspoeng? {
        val ar = dto.ar ?: return null
        val pensjonspoeng = dto.poeng ?: return null
        val pensjonspoengType = dto.pensjonspoengType ?: return null
        val pensjonsgivendeInntekt = dto.inntekt?.belop?.toInt() ?: return null

        return Pensjonspoeng(
            ar = ar,
            pensjonsgivendeInntekt = pensjonsgivendeInntekt,
            pensjonspoeng = pensjonspoeng,
            pensjonspoengType = pensjonspoengType,
            maksUforegrad = dto.maxUforegrad,
            omsorgspoeng = dto.omsorg?.ar
        )
    }
}
