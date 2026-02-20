package no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl

import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.RelasjonPersondata

object FamilierelasjonMapper {

    fun fromDto(dto: FamilierelasjonDto) =
        Familierelasjon(
            pid = dto.pid?.let(::Pid),
            fom = dto.fom,
            relasjonstype = dto.relasjonstype.internalValue,
            relasjonPersondata = dto.relasjonPersondata?.let(::relasjonPersondata)
        )

    private fun relasjonPersondata(dto: RelasjonPersondataDto) =
        RelasjonPersondata(
            tilgangsbegrensning = TilgangsbegrensningDto.internalValue(dto.tilgangsbegrensning),
            navn = dto.navn?.let(::navn),
            foedselsdato = dto.foedselsdato,
            doedsdato = dto.doedsdato,
            statsborgerskap = dto.statsborgerskap
        )

    private fun navn(dto: NavnDto) =
        Navn(
            fornavn = dto.fornavn,
            mellomnavn = dto.mellomnavn,
            etternavn = dto.etternavn
        )
}