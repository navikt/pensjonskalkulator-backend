package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto

import java.time.LocalDate

data class FppUtlandPeriodeDto(
    val fom: LocalDate,
    val tom: LocalDate?,
    val land: String,
    val arbeidetUtenlands: Boolean
)
