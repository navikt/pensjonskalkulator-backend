package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map

import no.nav.pensjon.kalkulator.tjenestepensjon.*
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.*

object TpTjenestepensjonMapper {

    fun fromDto(dto: TpApotekerDto): Boolean = dto.harLopendeForholdApotekerforeningen ?: false

    fun fromDto(dto: TpTjenestepensjonStatusDto): Boolean = dto.value ?: false

    fun fromDto(dto: TpTjenestepensjonDto): Tjenestepensjon =
        Tjenestepensjon(dto.forhold?.map(::forhold).orEmpty())

    fun fromDto(dto: FinnTjenestepensjonsforholdResponsDto): Tjenestepensjonsforhold =
        Tjenestepensjonsforhold(dto.forhold.orEmpty().map { it.ordning.navn })

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
