package no.nav.pensjon.kalkulator.tjenestepensjon

import java.time.LocalDate

data class FinnTjenestepensjonsforholdResponsDto(val fnr: String, val forhold: List<ForholdDto>? = emptyList())

data class ForholdDto(val ordning: OrdningDto, val ytelser: List<YtelseDto>?)

data class OrdningDto(val navn: String, val tpNr: String)

data class YtelseDto(val ytelseType: String, val datoInnmeldtYtelseFom: LocalDate?, val datoYtelseIverksattFom: LocalDate?, val datoYtelseIverksattTom: LocalDate?)