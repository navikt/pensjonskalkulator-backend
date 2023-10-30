package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto

import java.time.LocalDate

data class TpTjenestepensjonDto(val forhold: List<TpForholdDto>?)

data class TpForholdDto(
    val ordning: String?, // e.g. "3100"; if <3000 then utenlands
    val ytelser: List<TpYtelseDto>?,
    val datoSistOpptjening: LocalDate?
)

data class TpYtelseDto(
    val type: String?, // e.g. "ALDER"
    val datoInnmeldtYtelseFom: LocalDate?, // e.g. "2022-07-16"
    val datoYtelseIverksattFom: LocalDate?,
    val datoYtelseIverksattTom: LocalDate?
)
