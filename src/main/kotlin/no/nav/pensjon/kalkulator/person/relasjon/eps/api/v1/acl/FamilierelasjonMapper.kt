package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.relasjon.RelasjonPersondata

object FamilierelasjonMapper {

    fun toDto(source: Familierelasjon) =
        EpsV1Familierelasjon(
            pid = source.pid?.value,
            fom = source.fom,
            relasjonstype = EpsV1Relasjonstype.fromInternalValue(source.relasjonstype),
            relasjonPersondata = source.relasjonPersondata?.let(::relasjonPersondata)
        )

    private fun relasjonPersondata(source: RelasjonPersondata) =
        EpsV1RelasjonPersondata(
            tilgangsbegrensning = EpsV1Tilgangsbegrensning.fromInternalValue(source.tilgangsbegrensning),
            navn = source.navn?.let(::navn),
            foedselsdato = source.foedselsdato,
            doedsdato = source.doedsdato,
            statsborgerskap = source.statsborgerskap
        )

    private fun navn(source: Navn) =
        EpsV1Navn(
            fornavn = source.fornavn,
            mellomnavn = source.mellomnavn,
            etternavn = source.etternavn
        )
}