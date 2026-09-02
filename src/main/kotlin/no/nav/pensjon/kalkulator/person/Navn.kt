package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.person.NavnFormatter.formatNavn

data class Navn(
    val fornavn: String?,
    val mellomnavn: String? = null,
    val etternavn: String?
) {
    fun formatert(): String =
        formatNavn(fornavn, mellomnavn, etternavn)

    companion object {
        val ukjent = Navn(
            fornavn = "",
            mellomnavn = "",
            etternavn = "ukjent"
        )
    }
}