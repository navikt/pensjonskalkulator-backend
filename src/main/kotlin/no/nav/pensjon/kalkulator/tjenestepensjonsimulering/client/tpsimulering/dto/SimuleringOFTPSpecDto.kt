package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto

import java.time.LocalDate

data class SimuleringOFTPSpecDto(
    val pid: String,
    val foedselsdato: LocalDate,
    val uttaksdato: LocalDate,
    val sisteInntekt: Int,
    val aarIUtlandetEtter16: Int,
    val brukerBaOmAfp: Boolean,
    val epsPensjon: Boolean,
    val eps2G: Boolean,
    val fremtidigeInntekter: List<FremtidigInntektSimuleringOFTPSpecDto> = emptyList(),
    val erApoteker: Boolean
)

data class FremtidigInntektSimuleringOFTPSpecDto(val fraOgMed: LocalDate, val aarligInntekt: Int)
