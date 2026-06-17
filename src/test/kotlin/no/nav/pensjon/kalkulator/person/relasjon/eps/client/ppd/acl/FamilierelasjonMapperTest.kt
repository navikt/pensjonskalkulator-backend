package no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.Tilgangsbegrensning
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.RelasjonPersondata
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import java.time.LocalDate

class FamilierelasjonMapperTest : ShouldSpec({

    context("success") {
        should("mappe felter OK") {
            FamilierelasjonMapper.fromDto(
                dto()
            ) shouldBe Familierelasjon(
                pid = pid,
                fom = LocalDate.of(2021, 1, 1),
                relasjonstype = Relasjonstype.FRASEPARERT_EKTEFELLE,
                relasjonPersondata = RelasjonPersondata(
                    navn = Navn(fornavn = "f", mellomnavn = "m", etternavn = "e"),
                    foedselsdato = LocalDate.of(1970, 6, 15),
                    doedsdato = LocalDate.of(2025, 2, 20),
                    statsborgerskap = "AUS",
                    tilgangsbegrensning = Tilgangsbegrensning.FORTROLIG
                )
            )
        }
    }

    context("uforventet relasjonstype") {
        should("mappe til relasjonstype 'ukjent'") {
            FamilierelasjonMapper.fromDto(
                dto(relasjonstype = "UFORVENTET")
            ).relasjonstype shouldBe Relasjonstype.UKJENT
        }
    }

    context("uforventet tilgangsbegrensning") {
        should("mappe til tilgangsbegrensning 'unknown'") {
            FamilierelasjonMapper.fromDto(
                dto(tilgangsbegrensning = "UFORVENTET")
            ).relasjonPersondata?.tilgangsbegrensning shouldBe Tilgangsbegrensning.UNKNOWN
        }
    }
})

private fun dto(
    relasjonstype: String = "FRASEPARERT_EKTEFELLE",
    tilgangsbegrensning: String = "FORTROLIG"
) =
    FamilierelasjonDto(
        pid = pid.value,
        fom = LocalDate.of(2021, 1, 1),
        relasjonstype,
        relasjonPersondata = RelasjonPersondataDto(
            tilgangsbegrensning,
            navn = NavnDto(fornavn = "f", mellomnavn = "m", etternavn = "e"),
            foedselsdato = LocalDate.of(1970, 6, 15),
            doedsdato = LocalDate.of(2025, 2, 20),
            statsborgerskap = "AUS"
        )
    )