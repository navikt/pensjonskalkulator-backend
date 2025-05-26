package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService.Companion.defaultAldersgrenser
import java.time.LocalDate

data class Person(
    val navn: String,
    val foedselsdato: LocalDate,
    val pensjoneringAldre: Aldersgrenser = defaultAldersgrenser,
    val sivilstand: Sivilstand = Sivilstand.UOPPGITT,
    val adressebeskyttelse: AdressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT
) {
    val harFoedselsdato = foedselsdato >= minimumFoedselsdato

    fun withPensjoneringAldre(pensjoneringAldre: Aldersgrenser) =
        Person(
            navn,
            foedselsdato,
            pensjoneringAldre,
            sivilstand,
            adressebeskyttelse
        )

    private companion object {
        private val minimumFoedselsdato: LocalDate = LocalDate.of(1901, 1, 1)
    }
}
