package no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto

import java.math.BigDecimal
import java.time.LocalDate

data class GrunnbeloepResponseDto(val satsResultater : List<TidsbegrensetVerdiDto>?)

data class TidsbegrensetVerdiDto(val fom: LocalDate, val tom: LocalDate, val verdi: BigDecimal)
