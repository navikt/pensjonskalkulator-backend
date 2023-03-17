package no.nav.pensjon.kalkulator.person

import java.time.LocalDate

data class Person(val foedselsdato: LocalDate, val statsborgerskap: Land, val sivilstand: Sivilstand)
