package no.nav.pensjon.kalkulator.grunnbeloep.client

import java.time.LocalDate

data class GrunnbeloepSpec(
    val fom: LocalDate,
    val tom: LocalDate
)
