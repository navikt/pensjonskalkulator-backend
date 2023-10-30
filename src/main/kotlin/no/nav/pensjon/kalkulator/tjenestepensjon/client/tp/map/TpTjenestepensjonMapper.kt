package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map

import no.nav.pensjon.kalkulator.tjenestepensjon.Forhold
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjon
import no.nav.pensjon.kalkulator.tjenestepensjon.Ytelse
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpForholdDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpTjenestepensjonStatusDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpTjenestepensjonDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpYtelseDto

object TpTjenestepensjonMapper {

    fun fromDto(dto: TpTjenestepensjonStatusDto): Boolean = dto.value

    fun fromDto(dto: TpTjenestepensjonDto): Tjenestepensjon =
        Tjenestepensjon(dto.forhold?.map(::forhold).orEmpty())

    private fun forhold(dto: TpForholdDto): Forhold =
        Forhold(
            ordning = dto.ordning ?: "",
            ytelser = dto.ytelser?.map(::ytelse).orEmpty(),
            datoSistOpptjening = dto.datoSistOpptjening
        )

    private fun ytelse(dto: TpYtelseDto): Ytelse =
        Ytelse(
            type = dto.type ?: "",
            datoInnmeldtYtelseFom = dto.datoInnmeldtYtelseFom,
            datoYtelseIverksattFom = dto.datoYtelseIverksattFom,
            datoYtelseIverksattTom = dto.datoYtelseIverksattTom
        )
}
