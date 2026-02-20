package no.nav.pensjon.kalkulator.person.relasjon

import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.Tilgangsbegrensning
import java.time.LocalDate

data class RelasjonPersondata(
    val navn: Navn?,
    val foedselsdato: LocalDate?,
    val doedsdato: LocalDate?,
    val statsborgerskap: String?,
    val tilgangsbegrensning: Tilgangsbegrensning?
)