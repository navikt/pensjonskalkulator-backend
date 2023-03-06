package no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto

import java.time.LocalDate

data class SatsResultat(val fom: LocalDate, val tom: LocalDate, val verdi: Double)
