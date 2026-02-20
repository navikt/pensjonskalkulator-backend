package no.nav.pensjon.kalkulator.person.relasjon

import no.nav.pensjon.kalkulator.person.Pid
import java.time.LocalDate

data class Familierelasjon(
    val pid: Pid?,
    val fom: LocalDate?,
    val relasjonstype: Relasjonstype,
    val relasjonPersondata: RelasjonPersondata?
)