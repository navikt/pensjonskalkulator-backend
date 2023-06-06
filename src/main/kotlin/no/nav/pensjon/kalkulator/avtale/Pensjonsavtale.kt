package no.nav.pensjon.kalkulator.avtale

import java.time.LocalDate

data class Pensjonsavtale(val navn: String, val fom: LocalDate, val tom: LocalDate?)
