package no.nav.pensjon.kalkulator.person

import java.time.LocalDate

data class Person(
    val navn: String,
    val foedselsdato: LocalDate,
    val sivilstand: Sivilstand = Sivilstand.UOPPGITT,
    val adressebeskyttelse: AdressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT
) {
    val harFoedselsdato = foedselsdato >= minimumFoedselsdato

    private companion object {
        private val minimumFoedselsdato: LocalDate = LocalDate.of(1901, 1, 1)
    }
}
