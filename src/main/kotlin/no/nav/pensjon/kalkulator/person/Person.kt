package no.nav.pensjon.kalkulator.person

import java.time.LocalDate

data class Person(
    val fornavn: String?,
    val foedselsdato: LocalDate,
    val sivilstand: Sivilstand = Sivilstand.UOPPGITT
) {
    val harFoedselsdato = foedselsdato >= minimumFoedselsdato

    private companion object {
        val minimumFoedselsdato: LocalDate = LocalDate.of(1901, 1, 1)
    }
}
