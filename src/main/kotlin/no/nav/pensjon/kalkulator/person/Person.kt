package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.uttaksalder.normalder.NormertPensjoneringsalderService
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre
import java.time.LocalDate

data class Person(
    val navn: String,
    val foedselsdato: LocalDate,
    val pensjoneringAldre: PensjoneringAldre = NormertPensjoneringsalderService.defaultAldre,
    val sivilstand: Sivilstand = Sivilstand.UOPPGITT,
    val adressebeskyttelse: AdressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT
) {
    val harFoedselsdato = foedselsdato >= minimumFoedselsdato

    fun withPensjoneringAldre(pensjoneringAldre: PensjoneringAldre) =
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
