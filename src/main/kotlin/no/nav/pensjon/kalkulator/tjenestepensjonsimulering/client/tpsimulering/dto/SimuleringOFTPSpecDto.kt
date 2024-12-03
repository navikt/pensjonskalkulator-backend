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
)
