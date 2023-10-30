package no.nav.pensjon.kalkulator.tjenestepensjon

import java.time.LocalDate

data class Tjenestepensjon(val forholdList: List<Forhold>)

data class Forhold(
    val ordning: String,
    val ytelser: List<Ytelse>,
    val datoSistOpptjening: LocalDate?
)

data class Ytelse(
    val type: String,
    val datoInnmeldtYtelseFom: LocalDate?,
    val datoYtelseIverksattFom: LocalDate?,
    val datoYtelseIverksattTom: LocalDate?
)
