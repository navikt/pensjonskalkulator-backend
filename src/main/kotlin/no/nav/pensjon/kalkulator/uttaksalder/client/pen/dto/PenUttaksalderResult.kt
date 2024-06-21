package no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class PenUttaksalderResult(
    val alder: PenUttakAlder,
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Oslo") val dato: LocalDate
)

data class PenUttakAlder(
    val aar: Int,
    val maaneder: Int
)
