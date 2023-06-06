package no.nav.pensjon.kalkulator.avtale.api.dto

import java.time.LocalDate

data class PensjonsavtaleDto(val navn: String, val fom: LocalDate, val tom: LocalDate?)
