package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.person.NavnFormatter.formatNavn

data class Navn(
    val fornavn: String?,
    val mellomnavn: String? = null,
    val etternavn: String?,
    val erFormatert: Boolean = false
) {
    val formatert =
        if (erFormatert)
            this
        else
            Navn(
                fornavn = formatNavn(fornavn ?: ""),
                mellomnavn = formatNavn(mellomnavn ?: ""),
                etternavn = formatNavn(etternavn ?: ""),
                erFormatert = true
            )

    val formatertStreng: String = formatert.let {
        val foersteDel = "${it.fornavn} ${it.mellomnavn}".trim()
        "$foersteDel ${it.etternavn}".trim()
    }

    companion object {
        val ukjent = Navn(
            fornavn = "",
            mellomnavn = "",
            etternavn = "ukjent"
        )
    }
}