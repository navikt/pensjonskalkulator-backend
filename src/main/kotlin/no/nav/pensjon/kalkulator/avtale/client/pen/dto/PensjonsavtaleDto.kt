package no.nav.pensjon.kalkulator.avtale.client.pen.dto

import java.time.LocalDate

data class PensjonsavtaleDto(val navn: String, val fom: LocalDate, val tom: LocalDate?)
