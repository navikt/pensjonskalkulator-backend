package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto

import no.nav.pensjon.kalkulator.land.Land
import java.time.LocalDate

data class FppUtlandPeriodeDto(
    val fom: LocalDate,
    val tom: LocalDate?,
    val land: Land,
    val arbeidetUtenlands: Boolean
)